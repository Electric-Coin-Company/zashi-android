package co.electriccoin.zcash.ui.screen.integrations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetWalletRestoringStateUseCase
import co.electriccoin.zcash.ui.common.usecase.IsCoinbaseAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToCoinbaseUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import co.electriccoin.zcash.ui.screen.flexa.Flexa
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IntegrationsViewModel(
    getZcashCurrency: GetZcashCurrencyProvider,
    getWalletRestoringState: GetWalletRestoringStateUseCase,
    isFlexaAvailableUseCase: IsFlexaAvailableUseCase,
    isCoinbaseAvailable: IsCoinbaseAvailableUseCase,
    observeWalletAccounts: GetWalletAccountsUseCase,
    private val isDialog: Boolean,
    private val navigationRouter: NavigationRouter,
    private val navigateToCoinbase: NavigateToCoinbaseUseCase,
) : ViewModel() {
    val hideBottomSheetRequest = MutableSharedFlow<Unit>()

    private val bottomSheetHiddenResponse = MutableSharedFlow<Unit>()

    private val isRestoring = getWalletRestoringState.observe().map { it == WalletRestoringState.RESTORING }

    val state =
        combine(
            isFlexaAvailableUseCase.observe(),
            isCoinbaseAvailable.observe(),
            isRestoring,
            observeWalletAccounts.observe()
        ) { isFlexaAvailable, isCoinbaseAvailable, isRestoring, accounts ->
            IntegrationsState(
                disabledInfo =
                    stringRes(R.string.integrations_disabled_info)
                        .takeIf { isRestoring },
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
                        ).takeIf { isCoinbaseAvailable == true },
                        ZashiListItemState(
                            // Set the wallet currency by app build is more future-proof, although we hide it from
                            // the UI in the Testnet build
                            isEnabled = isRestoring.not(),
                            icon =
                                if (isRestoring.not()) {
                                    R.drawable.ic_integrations_flexa
                                } else {
                                    R.drawable.ic_integrations_flexa_disabled
                                },
                            title = stringRes(R.string.integrations_flexa),
                            subtitle = stringRes(R.string.integrations_flexa_subtitle),
                            onClick = ::onFlexaClicked
                        ).takeIf { isFlexaAvailable == true },
                        ZashiListItemState(
                            title = stringRes(R.string.integrations_keystone),
                            subtitle = stringRes(R.string.integrations_keystone_subtitle),
                            icon = R.drawable.ic_integrations_keystone,
                            onClick = ::onConnectKeystoneClick
                        ).takeIf { accounts.orEmpty().none { it is KeystoneAccount } },
                    ).toImmutableList(),
                onBottomSheetHidden = ::onBottomSheetHidden
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onBack() = navigationRouter.back()

    private suspend fun hideBottomSheet() {
        if (isDialog) {
            hideBottomSheetRequest.emit(Unit)
            bottomSheetHiddenResponse.first()
        }
    }

    private fun onBottomSheetHidden() =
        viewModelScope.launch {
            bottomSheetHiddenResponse.emit(Unit)
        }

    private fun onBuyWithCoinbaseClicked() =
        viewModelScope.launch {
            hideBottomSheet()
            navigateToCoinbase(isDialog)
        }

    private fun onConnectKeystoneClick() =
        viewModelScope.launch {
            hideBottomSheet()
            navigationRouter.replace(ConnectKeystone)
        }

    private fun onFlexaClicked() =
        viewModelScope.launch {
            if (isDialog) {
                hideBottomSheet()
                navigationRouter.replace(Flexa)
            } else {
                hideBottomSheet()
                navigationRouter.forward(Flexa)
            }
        }
}
