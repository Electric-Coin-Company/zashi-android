package co.electriccoin.zcash.ui.screen.home.backup

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState

data class WalletBackupInfoState(
    override val onBack: () -> Unit,
    val primaryButton: ButtonState,
    val secondaryButton: ButtonState
) : ModalBottomSheetState
