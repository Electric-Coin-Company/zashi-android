package co.electriccoin.zcash.ui.screen.integrations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetCoinbaseStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetKeystoneStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.Status
import co.electriccoin.zcash.ui.common.usecase.Status.UNAVAILABLE
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsVM(
    getZcashCurrency: GetZcashCurrencyProvider,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    getCoinbaseStatus: GetCoinbaseStatusUseCase,
    getKeystoneStatus: GetKeystoneStatusUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase,
    private val navigateToSwap: NavigateToSwapUseCase,
) : ViewModel() {
    private val isRestoring = getWalletRestoringState.observe().map { it == WalletRestoringState.RESTORING }

    val state =
        combine(
            isRestoring,
            getSelectedWalletAccount.observe(),
            getCoinbaseStatus.observe(),
            getKeystoneStatus.observe(),
        ) { isRestoring, selectedAccount, coinbaseStatus, keystoneStatus ->
            createState(
                isRestoring = isRestoring,
                getZcashCurrency = getZcashCurrency,
                selectedAccount = selectedAccount,
                coinbaseStatus = coinbaseStatus,
                keystoneStatus = keystoneStatus,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun createState(
        isRestoring: Boolean,
        getZcashCurrency: GetZcashCurrencyProvider,
        selectedAccount: WalletAccount?,
        coinbaseStatus: Status,
        keystoneStatus: Status
    ) = IntegrationsState(
        disabledInfo =
            when {
                isRestoring -> stringRes(R.string.integrations_disabled_info)
                selectedAccount is KeystoneAccount -> stringRes(R.string.integrations_disabled_info_flexa)
                else -> null
            },
        onBack = ::onBack,
        items =
            listOfNotNull(
                ListItemState(
                    bigIcon = imageRes(R.drawable.ic_integrations_near),
                    title = stringRes(R.string.integrations_near_swap),
                    subtitle = stringRes(R.string.integrations_near_swap_message),
                    onClick = ::onNearSwapClick,
                ),
                ListItemState(
                    // Set the wallet currency by app build is more future-proof, although we hide it from
                    // the UI in the Testnet build
                    bigIcon = imageRes(R.drawable.ic_integrations_coinbase),
                    title = stringRes(R.string.integrations_coinbase, getZcashCurrency.getLocalizedName()),
                    subtitle =
                        stringRes(
                            R.string.integrations_coinbase_subtitle,
                            getZcashCurrency.getLocalizedName()
                        ),
                    onClick = ::onBuyWithCoinbaseClicked
                ).takeIf { coinbaseStatus != UNAVAILABLE },
                ListItemState(
                    title = stringRes(R.string.integrations_keystone),
                    subtitle = stringRes(R.string.integrations_keystone_subtitle),
                    bigIcon = imageRes(R.drawable.ic_integrations_keystone),
                    onClick = ::onConnectKeystoneClick
                ).takeIf { keystoneStatus != UNAVAILABLE },
            ).toImmutableList(),
    )

    private fun onNearSwapClick() = viewModelScope.launch { navigateToSwap() }

    private fun onBack() = navigationRouter.back()

    private fun onBuyWithCoinbaseClicked() = viewModelScope.launch { navigateToCoinbase() }

    private fun onConnectKeystoneClick() = viewModelScope.launch { navigationRouter.replace(ConnectKeystone) }
}
