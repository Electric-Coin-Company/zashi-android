package co.electriccoin.zcash.ui.screen.integrations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.GetFlexaStatusUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToNearPayUseCase
import co.electriccoin.zcash.ui.common.usecase.Status
import co.electriccoin.zcash.ui.common.usecase.Status.DISABLED
import co.electriccoin.zcash.ui.common.usecase.Status.ENABLED
import co.electriccoin.zcash.ui.common.usecase.Status.UNAVAILABLE
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.flexa.Flexa
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PayIntegrationsVM(
    getFlexaStatus: GetFlexaStatusUseCase,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
    private val navigateToNearPay: NavigateToNearPayUseCase
) : ViewModel() {

    private val isRestoring = getWalletRestoringState.observe().map { it == WalletRestoringState.RESTORING }

    val state = combine(
        getFlexaStatus.observe(),
        isRestoring,
        getSelectedWalletAccount.observe()
    ) { flexaStatus, isRestoring, selectedAccount ->
        createState(
            flexaStatus = flexaStatus,
            isRestoring = isRestoring,
            selectedAccount = selectedAccount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = null
    )

    private fun createState(
        isRestoring: Boolean,
        selectedAccount: WalletAccount?,
        flexaStatus: Status
    ) = IntegrationsState(
        disabledInfo = null,
        onBack = ::onBack,
        items =
            listOfNotNull(
                ListItemState(
                    bigIcon = imageRes(R.drawable.ic_integrations_near),
                    title = stringRes("CrossPay with Near"),
                    subtitle = stringRes("Use shielded ZEC to send cross-chain payments."),
                    onClick = ::onNearPayClick,
                ),
                ListItemState(
                    // Set the wallet currency by app build is more future-proof, although we hide it from
                    // the UI in the Testnet build
                    isEnabled = isRestoring.not() && selectedAccount is ZashiAccount,
                    bigIcon =
                        imageRes(
                            when (flexaStatus) {
                                ENABLED -> R.drawable.ic_integrations_flexa
                                DISABLED -> R.drawable.ic_integrations_flexa_disabled
                                UNAVAILABLE -> R.drawable.ic_integrations_flexa_disabled
                            }
                        ),
                    title = stringRes(R.string.integrations_flexa),
                    subtitle = stringRes(R.string.integrations_flexa_subtitle),
                    onClick = ::onFlexaClicked
                ).takeIf { flexaStatus != UNAVAILABLE },
            ).toImmutableList(),
    )

    private fun onNearPayClick() = viewModelScope.launch { navigateToNearPay() }

    private fun onFlexaClicked() = navigationRouter.replace(Flexa)

    private fun onBack() = navigationRouter.back()
}
