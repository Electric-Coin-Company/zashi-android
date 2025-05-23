package co.electriccoin.zcash.ui.screen.integrations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetCoinbaseStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetFlexaStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetKeystoneStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.common.usecase.Status
import co.electriccoin.zcash.ui.common.usecase.Status.DISABLED
import co.electriccoin.zcash.ui.common.usecase.Status.ENABLED
import co.electriccoin.zcash.ui.common.usecase.Status.UNAVAILABLE
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import co.electriccoin.zcash.ui.screen.flexa.Flexa
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsViewModel(
    getZcashCurrency: GetZcashCurrencyProvider,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    getCoinbaseStatus: GetCoinbaseStatusUseCase,
    getFlexaStatus: GetFlexaStatusUseCase,
    getKeystoneStatus: GetKeystoneStatusUseCase,
    private val isDialog: Boolean,
    private val navigationRouter: NavigationRouter,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase,
) : ViewModel() {
    private val isRestoring = getWalletRestoringState.observe().map { it == WalletRestoringState.RESTORING }

    val state =
        combine(
            isRestoring,
            getSelectedWalletAccount.observe(),
            getCoinbaseStatus.observe(),
            getFlexaStatus.observe(),
            getKeystoneStatus.observe(),
        ) { isRestoring, selectedAccount, coinbaseStatus, flexaStatus, keystoneStatus ->
            createState(
                isRestoring = isRestoring,
                getZcashCurrency = getZcashCurrency,
                selectedAccount = selectedAccount,
                flexaStatus = flexaStatus,
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
        flexaStatus: Status,
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
                ZashiListItemState(
                    // Set the wallet currency by app build is more future-proof, although we hide it from
                    // the UI in the Testnet build
                    icon = R.drawable.ic_integrations_coinbase,
                    title = stringRes(R.string.integrations_coinbase, getZcashCurrency.getLocalizedName()),
                    subtitle =
                        stringRes(
                            R.string.integrations_coinbase_subtitle,
                            getZcashCurrency.getLocalizedName()
                        ),
                    onClick = ::onBuyWithCoinbaseClicked
                ).takeIf { coinbaseStatus != UNAVAILABLE },
                ZashiListItemState(
                    // Set the wallet currency by app build is more future-proof, although we hide it from
                    // the UI in the Testnet build
                    isEnabled = isRestoring.not() && selectedAccount is ZashiAccount,
                    icon =
                        when (flexaStatus) {
                            ENABLED -> R.drawable.ic_integrations_flexa
                            DISABLED -> R.drawable.ic_integrations_flexa_disabled
                            UNAVAILABLE -> R.drawable.ic_integrations_flexa_disabled
                        },
                    title = stringRes(R.string.integrations_flexa),
                    subtitle = stringRes(R.string.integrations_flexa_subtitle),
                    onClick = ::onFlexaClicked
                ).takeIf { flexaStatus != UNAVAILABLE },
                ZashiListItemState(
                    title = stringRes(R.string.integrations_keystone),
                    subtitle = stringRes(R.string.integrations_keystone_subtitle),
                    icon = R.drawable.ic_integrations_keystone,
                    onClick = ::onConnectKeystoneClick
                ).takeIf { keystoneStatus != UNAVAILABLE },
            ).toImmutableList(),
    )

    private fun onBack() = navigationRouter.back()

    private fun onBuyWithCoinbaseClicked() =
        viewModelScope.launch {
            navigateToCoinbase(isDialog)
        }

    private fun onConnectKeystoneClick() =
        viewModelScope.launch {
            navigationRouter.replace(ConnectKeystone)
        }

    private fun onFlexaClicked() {
        if (isDialog) {
            navigationRouter.replace(Flexa)
        } else {
            navigationRouter.forward(Flexa)
        }
    }
}
