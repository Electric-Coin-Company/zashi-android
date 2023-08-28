package co.electriccoin.zcash.ui.screen.shield.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUIState
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUiDestination
import co.electriccoin.zcash.ui.screen.shield.model.ShieldingProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class ShieldViewModel(val context: Application): AndroidViewModel(application = context) {

    private val maxAutoShieldFrequency: Long = 30.minutes.inWholeMilliseconds
    private val _shieldUiState = MutableStateFlow<ShieldUIState>(ShieldUIState.Loading)
    val shieldUIState: StateFlow<ShieldUIState> get() = _shieldUiState

    private val _shieldingProcessSate: MutableStateFlow<ShieldingProcessState> = MutableStateFlow(ShieldingProcessState.CREATING)
    val shieldingProcessState: StateFlow<ShieldingProcessState> get() = _shieldingProcessSate

    fun checkAutoShieldUiState() {
        viewModelScope.launch(Dispatchers.IO) {
            val canAutoShield = canAutoShield()
            val isAutoShieldAcknowledged = isAutoshieldingAcknowledged()
            if (canAutoShield) {
                if (isAutoShieldAcknowledged.not()) {
                    _shieldUiState.update { ShieldUIState.OnResult(ShieldUiDestination.AutoShieldingInfo) }
                } else {
                    _shieldUiState.update { ShieldUIState.OnResult(ShieldUiDestination.ShieldFunds) }
                }
            } else {
                _shieldUiState.update { ShieldUIState.OnResult(ShieldUiDestination.AutoShieldError("Try again after ${maxAutoShieldFrequency.milliseconds.inWholeMinutes} minutes of your last auto shielding")) }
            }
        }
    }

    fun getCurrentDestination(): ShieldUiDestination? {
        return when(val uiState = _shieldUiState.value) {
            ShieldUIState.Loading -> null
            is ShieldUIState.OnResult -> uiState.destination
        }
    }

    fun acknowledgeShieldingInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val encryptionProvider = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.IS_AUTOSHIELDING_INFO_ACKNOWLEDGED.putValue(encryptionProvider, true)
        }
    }

    fun updateLastAutoShieldTime() {
        viewModelScope.launch(Dispatchers.IO) {
            val encryptionProvider = StandardPreferenceSingleton.getInstance(context)
            StandardPreferenceKeys.LAST_AUTOSHIELDING_PROMPT_EPOCH_MILLIS_STRING.putValue(encryptionProvider, "${Clock.System.now().toEpochMilliseconds()}")
        }
    }

    fun updateShieldUiState(shieldUIState: ShieldUIState) {
        _shieldUiState.update { shieldUIState }
    }

    fun updateShieldingProcessState(shieldingProcessState: ShieldingProcessState) {
        _shieldingProcessSate.update { shieldingProcessState }
    }

    fun clearData() {
        _shieldingProcessSate.update { ShieldingProcessState.CREATING }
        _shieldUiState.update { ShieldUIState.Loading }
    }

    private suspend fun canAutoShield(): Boolean {
        val currentEpochMillis = Clock.System.now().toEpochMilliseconds()
        val encryptionProvider = StandardPreferenceSingleton.getInstance(context)
        val lastAutoShieldEpochMillis = StandardPreferenceKeys.LAST_AUTOSHIELDING_PROMPT_EPOCH_MILLIS_STRING.getValue(encryptionProvider).toLongOrNull()
        return (currentEpochMillis - (lastAutoShieldEpochMillis ?: 0L)) > maxAutoShieldFrequency
    }

    private suspend fun isAutoshieldingAcknowledged(): Boolean {
        val encryptionProvider = StandardPreferenceSingleton.getInstance(context)
        return StandardPreferenceKeys.IS_AUTOSHIELDING_INFO_ACKNOWLEDGED.getValue(encryptionProvider)
    }

}
