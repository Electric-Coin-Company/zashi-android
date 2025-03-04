package co.electriccoin.zcash.ui.screen.transactionnote.model

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StyledStringResource

data class TransactionNoteState(
    val onBack: () -> Unit,
    val onBottomSheetHidden: () -> Unit,
    val title: StringResource,
    val note: TextFieldState,
    val noteCharacters: StyledStringResource,
    val primaryButton: ButtonState?,
    val secondaryButton: ButtonState?,
    val negative: ButtonState?,
)
