package co.electriccoin.zcash.ui.common.appbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.BooleanPreferenceDefault
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets.SETTINGS
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletAccountsUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.accountlist.AccountList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ZashiTopAppBarViewModel(
    getWalletAccountUseCase: GetWalletAccountsUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val isHideBalances: StateFlow<Boolean?> = booleanStateFlow(StandardPreferenceKeys.IS_HIDE_BALANCES)

    val state =
        combine(
            getSelectedWalletAccount.observe().filterNotNull(),
            isHideBalances,
        ) { currentAccount, isHideBalances ->
            createState(currentAccount, isHideBalances)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                createState(
                    currentAccount = getWalletAccountUseCase.observe().value?.firstOrNull { it.isSelected },
                    isHideBalances = isHideBalances.value
                )
        )

    private fun createState(
        currentAccount: WalletAccount?,
        isHideBalances: Boolean?
    ) = ZashiMainTopAppBarState(
        accountSwitchState =
            AccountSwitchState(
                accountType =
                    when (currentAccount) {
                        is KeystoneAccount -> ZashiMainTopAppBarState.AccountType.KEYSTONE
                        is ZashiAccount -> ZashiMainTopAppBarState.AccountType.ZASHI
                        else -> ZashiMainTopAppBarState.AccountType.ZASHI
                    },
                onAccountTypeClick = ::onAccountTypeClicked,
            ),
        balanceVisibilityButton =
            IconButtonState(
                icon =
                    if (isHideBalances == true) {
                        R.drawable.ic_app_bar_balances_show
                    } else {
                        R.drawable.ic_app_bar_balances_hide
                    },
                onClick = ::onShowOrHideBalancesClicked,
                contentDescription = stringRes(co.electriccoin.zcash.ui.R.string.hide_balances_content_description)
            ),
        settingsButton =
            IconButtonState(
                icon = R.drawable.ic_app_bar_settings,
                onClick = ::onSettingsClicked,
                contentDescription = stringRes(co.electriccoin.zcash.ui.R.string.settings_menu_content_description)
            )
    )

    private fun onAccountTypeClicked() = navigationRouter.forward(AccountList)

    private fun onSettingsClicked() = navigationRouter.forward(SETTINGS)

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
