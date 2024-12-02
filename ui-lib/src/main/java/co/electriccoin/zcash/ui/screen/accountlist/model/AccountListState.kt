package co.electriccoin.zcash.ui.screen.accountlist.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState

data class AccountListState(
    val accounts: List<ZashiSettingsListItemState>?,
    val isLoading: Boolean,
    val addWalletButton: ButtonState
)
