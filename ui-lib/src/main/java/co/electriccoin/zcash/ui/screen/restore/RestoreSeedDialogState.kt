package co.electriccoin.zcash.ui.screen.restore

import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

data class RestoreSeedDialogState(
    override val onBack: () -> Unit
) : ModalBottomSheetState
