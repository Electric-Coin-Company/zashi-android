package co.electriccoin.zcash.ui.screen.accountlist.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiListItemState

data class AccountListState(
    val accounts: List<ZashiListItemState>?,
    val isLoading: Boolean,
    val addWalletButton: ButtonState
)
