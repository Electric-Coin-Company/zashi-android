package co.electriccoin.zcash.ui.screen.request.model

import cash.z.ecc.android.sdk.model.Zatoshi

data class Request(
    val amountState: AmountState,
    val memoState: MemoState,
)

sealed class AmountState(open val amount: Zatoshi) {
    data class Valid(override val amount: Zatoshi) : AmountState(amount)
    data class InValid(override val amount: Zatoshi) : AmountState(amount)
}

sealed class MemoState() {
    data class Valid(val value: String) : MemoState()
    data class InValid(val value: String) : MemoState()
}