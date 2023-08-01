package co.electriccoin.zcash.ui.screen.home.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.global.DeepLinkUtil
import co.electriccoin.zcash.ui.common.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * A flow of whether background sync is enabled
     */
    val isBackgroundSyncEnabled: StateFlow<Boolean?> = flow {
        val preferenceProvider = StandardPreferenceSingleton.getInstance(application)
        emitAll(StandardPreferenceKeys.IS_BACKGROUND_SYNC_ENABLED.observe(preferenceProvider))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds), null)

    val configurationFlow: StateFlow<Configuration?> =
        AndroidConfigurationFactory.getInstance(application).getConfigurationFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT.inWholeMilliseconds),
                null
            )

    /**
     * A flow of whether transfer tab is enabled. We disable the transfer tab in sync state
     */
    private val _isTransferTabEnabled = MutableStateFlow(false)
    val isTransferStateEnabled: StateFlow<Boolean> get() = _isTransferTabEnabled

    var intentDataUriForDeepLink: Uri? = null
    var sendDeepLinkData: DeepLinkUtil.SendDeepLinkData? = null
    // Flag to track any expecting balance is there or not. We will show snackBar everytime user open the app until it is a confirmed transaction
    var expectingZatoshi = 0L

    fun onTransferTabStateChanged(enable: Boolean) {
        _isTransferTabEnabled.update { enable }
    }

    fun isAnyExpectingTransaction(walletSnapshot: WalletSnapshot): Boolean {
        val totalBalance = walletSnapshot.saplingBalance.total + walletSnapshot.transparentBalance.total
        val availableBalance = walletSnapshot.saplingBalance.available + walletSnapshot.transparentBalance.available
        if (totalBalance != availableBalance && ((totalBalance - availableBalance).value != expectingZatoshi)) {
            expectingZatoshi = (totalBalance - availableBalance).value
            return true
        }
        return false
    }
}
