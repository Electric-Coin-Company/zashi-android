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

data class Request(
    val amountState: AmountState,
    val memoState: MemoState,
    val qrCodeState: QrCodeState,
)

sealed class AmountState(
    open val amount: String,
    open val currency: RequestCurrency
) {
    fun isValid(): Boolean = this is Valid

    abstract fun copyState(
        newValue: String = amount,
        newCurrency: RequestCurrency = currency
    ): AmountState

    fun toZecString(conversion: FiatCurrencyConversion): String =
        runCatching {
            amount.convertToDouble().convertUsdToZec(conversion.priceOfZec).toZecString()
        }.getOrElse { "" }

    fun toFiatString(
        context: Context,
        conversion: FiatCurrencyConversion
    ): String =
        kotlin
            .runCatching {
                Zatoshi.fromZecString(context, amount, Locale.getDefault())?.toFiatString(
                    currencyConversion = conversion,
                    locale = Locale.getDefault()
                ) ?: ""
            }.getOrElse { "" }

    data class Valid(
        override val amount: String,
        override val currency: RequestCurrency
    ) : AmountState(amount, currency) {
        override fun copyState(
            newValue: String,
            newCurrency: RequestCurrency
        ) = copy(amount = newValue, currency = newCurrency)
    }

    data class Default(
        override val amount: String,
        override val currency: RequestCurrency
    ) : AmountState(amount, currency) {
        override fun copyState(
            newValue: String,
            newCurrency: RequestCurrency
        ) = copy(amount = newValue, currency = newCurrency)
    }

    data class InValid(
        override val amount: String,
        override val currency: RequestCurrency
    ) : AmountState(amount, currency) {
        override fun copyState(
            newValue: String,
            newCurrency: RequestCurrency
        ) = copy(amount = newValue, currency = newCurrency)
    }
}

sealed class MemoState(
    open val text: String,
    open val byteSize: Int,
    open val zecAmount: String
) {
    fun isValid(): Boolean = this is Valid

    data class Valid(
        override val text: String,
        override val byteSize: Int,
        override val zecAmount: String
    ) : MemoState(text, byteSize, zecAmount)

    data class InValid(
        override val text: String,
        override val byteSize: Int,
        override val zecAmount: String
    ) : MemoState(text, byteSize, zecAmount)

    companion object {
        fun new(
            memo: String,
            amount: String
        ): MemoState {
            val bytesCount = Memo.countLength(memo)
            return if (bytesCount > Memo.MAX_MEMO_LENGTH_BYTES) {
                InValid(memo, bytesCount, amount)
            } else {
                Valid(memo, bytesCount, amount)
            }
        }
    }
}

data class QrCodeState(
    val requestUri: String,
    val zecAmount: String,
    val memo: String
)
