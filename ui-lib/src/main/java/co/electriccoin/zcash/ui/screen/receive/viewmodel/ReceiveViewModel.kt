package co.electriccoin.zcash.ui.screen.receive.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.NavigationTargets
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressState
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ReceiveViewModel(
    observeSelectedWalletAccount: ObserveSelectedWalletAccountUseCase,
    private val application: Application,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val expandedIndex = MutableStateFlow(0)

    internal val state =
        combine(expandedIndex, observeSelectedWalletAccount.require()) { expandedIndex, account ->
            ReceiveState(
                items =
                    listOfNotNull(
                        createAddressState(
                            account = account,
                            address = account.unified.address.address,
                            type = ReceiveAddressType.Unified,
                            isExpanded = expandedIndex == 0,
                            onClick = { onAddressClick(0) }
                        ),
                        createAddressState(
                            account = account,
                            address = account.transparent.address.address,
                            type = ReceiveAddressType.Transparent,
                            isExpanded = expandedIndex == 1,
                            onClick = { onAddressClick(1) }
                        ),
                    ),
                isLoading = false,
                onBack = ::onBack
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue =
                ReceiveState(
                    items = null,
                    isLoading = true,
                    onBack = ::onBack
                )
        )

    private fun onBack() = navigationRouter.back()

    private fun createAddressState(
        account: WalletAccount,
        address: String,
        type: ReceiveAddressType,
        isExpanded: Boolean,
        onClick: () -> Unit,
    ) = ReceiveAddressState(
        icon =
            when (account) {
                is KeystoneAccount -> co.electriccoin.zcash.ui.design.R.drawable.ic_item_keystone
                is ZashiAccount -> R.drawable.ic_zec_round_full
            },
        title =
            when (account) {
                is KeystoneAccount ->
                    if (type == ReceiveAddressType.Unified) {
                        stringRes(R.string.receive_wallet_address_shielded_keystone)
                    } else {
                        stringRes(R.string.receive_wallet_address_transparent_keystone)
                    }

                is ZashiAccount ->
                    if (type == ReceiveAddressType.Unified) {
                        stringRes(R.string.receive_wallet_address_shielded)
                    } else {
                        stringRes(R.string.receive_wallet_address_transparent)
                    }
            },
        subtitle = stringRes("${address.take(ADDRESS_MAX_LENGTH)}..."),
        isShielded = type == ReceiveAddressType.Unified,
        onCopyClicked = {
            copyToClipboard(
                tag = application.getString(R.string.receive_clipboard_tag),
                value = address
            )
        },
        onQrClicked = { onQrCodeClick(type) },
        onRequestClicked = { onRequestClick(type) },
        onClick = onClick,
        isExpanded = isExpanded
    )

    private fun onRequestClick(addressType: ReceiveAddressType) =
        navigationRouter.forward("${NavigationTargets.REQUEST}/${addressType.ordinal}")

    private fun onQrCodeClick(addressType: ReceiveAddressType) =
        navigationRouter.forward("${NavigationTargets.QR_CODE}/${addressType.ordinal}")

    private fun onAddressClick(index: Int) {
        expandedIndex.update { index }
    }
}
