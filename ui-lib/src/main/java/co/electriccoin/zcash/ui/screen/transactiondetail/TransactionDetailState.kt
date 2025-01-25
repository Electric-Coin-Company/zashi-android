package co.electriccoin.zcash.ui.screen.transactiondetail

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.screen.transactiondetail.info.TransactionDetailInfoState

data class TransactionDetailState(
    val onBack: () -> Unit,
    // val bookmarkButton: IconButtonState,
    val header: TransactionDetailHeaderState,
    val info: TransactionDetailInfoState,
    val primaryButton: ButtonState?,
    val secondaryButton: ButtonState?
)
