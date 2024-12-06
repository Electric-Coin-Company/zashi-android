package co.electriccoin.zcash.ui.screen.selectkeystoneaccount.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.checkbox.CheckboxListItemState
import co.electriccoin.zcash.ui.design.util.StringResource

data class SelectKeystoneAccountState(
    val onBackClick: () -> Unit,
    val title: StringResource,
    val subtitle: StringResource,
    val items: List<CheckboxListItemState>,
    val positiveButtonState: ButtonState,
    val negativeButtonState: ButtonState
)
