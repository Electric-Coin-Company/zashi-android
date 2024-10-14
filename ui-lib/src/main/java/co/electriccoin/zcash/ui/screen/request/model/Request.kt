package co.electriccoin.zcash.ui.screen.request.model

import android.content.Context
import cash.z.ecc.android.sdk.ext.convertUsdToZec
import cash.z.ecc.android.sdk.ext.toZecString
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.fromZecString
import cash.z.ecc.android.sdk.model.toFiatString
import co.electriccoin.zcash.ui.screen.request.ext.convertToDouble
import co.electriccoin.zcash.ui.screen.request.model.AmountState.Valid

data class Request(
    val amountState: AmountState,
    val memoState: MemoState,
)

sealed class AmountState(open val amount: String) {
    fun isValid(): Boolean = this is Valid

    abstract fun copyState(newValue: String): AmountState

    fun toZecString(
        conversion: FiatCurrencyConversion
    ): String = runCatching {
        amount.convertToDouble().convertUsdToZec(conversion.priceOfZec).toZecString()
    }.getOrElse { "" }

    fun toFiatString(
        context: Context,
        conversion: FiatCurrencyConversion
    ): String = kotlin.runCatching {
        Zatoshi.fromZecString(context, amount, Locale.getDefault())?.toFiatString(
            currencyConversion = conversion,
            locale = Locale.getDefault()
        ) ?: ""
    }.getOrElse { "" }

    data class Valid(override val amount: String) : AmountState(amount) {
        override fun copyState(newValue: String) = copy(amount = newValue)
    }
    data class Default(override val amount: String) : AmountState(amount) {
        override fun copyState(newValue: String) = copy(amount = newValue)
    }
    data class InValid(override val amount: String) : AmountState(amount) {
        override fun copyState(newValue: String) = copy(amount = newValue)
    }
}

sealed class MemoState(
    open val text: String,
    open val byteSize: Int
) {
    fun isValid(): Boolean = this is Valid

    data class Valid(
        override val text: String,
        override val byteSize: Int
    ) : MemoState(text, byteSize)

    data class InValid(
        override val text: String,
        override val byteSize: Int
    ) : MemoState(text, byteSize)

    companion object {
        fun new(memo: String): MemoState {
            val bytesCount = Memo.countLength(memo)
            return if (bytesCount > Memo.MAX_MEMO_LENGTH_BYTES) {
                InValid(memo, bytesCount)
            } else {
                Valid(memo, bytesCount)
            }
        }
    }
}