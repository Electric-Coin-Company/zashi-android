package co.electriccoin.zcash.ui.screen.reviewtransaction

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiChipButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class ReviewTransactionState(
    val title: StringResource,
    val items: List<ReviewTransactionItemState>,
    val primaryButton: ButtonState,
    val negativeButton: ButtonState,
    val onBack: () -> Unit,
)

sealed interface ReviewTransactionItemState

data class AmountState(
    val title: StringResource?,
    val amount: Zatoshi,
    val exchangeRate: ExchangeRateState,
) : ReviewTransactionItemState

data class ReceiverState(
    val title: StringResource,
    val name: StringResource?,
    val address: StringResource,
) : ReviewTransactionItemState

data class ReceiverExpandedState(
    val title: StringResource,
    val name: StringResource?,
    val address: StringResource,
    val showButton: ZashiChipButtonState,
    val saveButton: ZashiChipButtonState?
) : ReviewTransactionItemState

data class SenderState(
    val title: StringResource,
    val icon: Int,
    val name: StringResource
) : ReviewTransactionItemState

data class FinancialInfoState(
    val title: StringResource,
    val amount: Zatoshi,
) : ReviewTransactionItemState

data class MessageState(
    val title: StringResource,
    val message: StringResource
) : ReviewTransactionItemState
