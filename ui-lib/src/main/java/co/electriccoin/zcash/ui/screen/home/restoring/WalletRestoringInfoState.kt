package co.electriccoin.zcash.ui.screen.home.restoring

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.ModalBottomSheetState
import co.electriccoin.zcash.ui.design.util.StringResource

@Immutable
data class WalletRestoringInfoState(
    val info: StringResource?,
    override val onBack: () -> Unit
) : ModalBottomSheetState
