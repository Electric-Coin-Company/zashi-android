package co.electriccoin.zcash.ui.screen.accountlist.model

import androidx.annotation.DrawableRes
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.StringResource

data class AccountListState(
    val items: List<AccountListItem>?,
    val isLoading: Boolean,
    val addWalletButton: ButtonState?,
    override val onBack: () -> Unit,
) : ModalBottomSheetState

data class ZashiAccountListItemState(
    @DrawableRes val icon: Int,
    val title: StringResource,
    val subtitle: StringResource,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

sealed interface AccountListItem {
    data class Account(
        val state: ZashiAccountListItemState
    ) : AccountListItem

    data class Other(
        val state: ZashiListItemState
    ) : AccountListItem
}
