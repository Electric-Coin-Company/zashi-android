package co.electriccoin.zcash.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.accountlist.AccountListArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ZashiMainTopAppBarViewModel(
    private val standardPreferenceProvider: StandardPreferenceProvider,
) : ViewModel() {
    private val isHideBalances: StateFlow<Boolean?> = booleanStateFlow(StandardPreferenceKeys.IS_HIDE_BALANCES)

    val navigationCommand = MutableSharedFlow<String>()

    val state =
        isHideBalances.map {
            createState(it)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), createState(false))

    private fun createState(it: Boolean?) =
        ZashiMainTopAppBarState(
            accountType = ZashiMainTopAppBarState.AccountType.ZASHI,
            balanceVisibilityButton =
                IconButtonState(
                    icon = if (it == true) R.drawable.ic_hide_balances_on else R.drawable.ic_hide_balances_off,
                    onClick = ::onShowOrHideBalancesClicked
                ),
            settingsButton =
                IconButtonState(
                    icon = R.drawable.ic_app_bar_settings,
                    onClick = ::onSettingsClicked
                ),
            onAccountTypeClick = ::onAccountTypeClicked
        )

    private fun onAccountTypeClicked() =
        viewModelScope.launch {
            navigationCommand.emit(AccountListArgs.PATH)
        }

    private fun onSettingsClicked() =
        viewModelScope.launch {
            navigationCommand.emit(SETTINGS)
        }

    private fun onShowOrHideBalancesClicked() =
        viewModelScope.launch {
            setBooleanPreference(StandardPreferenceKeys.IS_HIDE_BALANCES, isHideBalances.filterNotNull().first().not())
        }

    private fun booleanStateFlow(default: BooleanPreferenceDefault): StateFlow<Boolean?> =
        flow<Boolean?> {
            emitAll(default.observe(standardPreferenceProvider()))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            null
        )

    private fun setBooleanPreference(
        default: BooleanPreferenceDefault,
        newState: Boolean
    ) {
        viewModelScope.launch {
            default.putValue(standardPreferenceProvider(), newState)
        }
    }
}
