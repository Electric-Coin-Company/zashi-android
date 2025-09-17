package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.annotation.DrawableRes
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ChipButtonState
import co.electriccoin.zcash.ui.design.component.SwapQuoteHeaderState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.StyledStringResource
import co.electriccoin.zcash.ui.design.util.styledStringResource

data class ReviewTransactionState(
    val title: StringResource,
    val items: List<ReviewTransactionItemState>,
    val primaryButton: ButtonState,
    val onBack: () -> Unit,
)

sealed interface ReviewTransactionItemState

data class AmountState(
    val title: StringResource?,
    val amount: Zatoshi,
    val exchangeRate: ExchangeRateState,
) : ReviewTransactionItemState

data class ExactOutputQuoteState(
    val state: SwapQuoteHeaderState
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
    val showButton: ChipButtonState,
    val saveButton: ChipButtonState?
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

data class SimpleListItemState(
    val title: StyledStringResource,
    val text: StyledStringResource,
    val subtext: StyledStringResource?
) : ReviewTransactionItemState

data class MessageState(
    val title: StringResource,
    val message: StringResource
) : ReviewTransactionItemState

data class MessagePlaceholderState(
    @DrawableRes val icon: Int,
    val title: StringResource,
    val message: StringResource,
) : ReviewTransactionItemState

data object DividerState : ReviewTransactionItemState
