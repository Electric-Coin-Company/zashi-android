package co.electriccoin.zcash.ui.screen.request.model

import android.content.Context
import cash.z.ecc.android.sdk.ext.convertUsdToZec
import cash.z.ecc.android.sdk.ext.toZecString
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.fromZecString
import cash.z.ecc.android.sdk.model.toFiatString
import co.electriccoin.zcash.ui.screen.request.ext.convertToDouble

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

sealed class MemoState() {
    data class Valid(val value: String) : MemoState()
    data class InValid(val value: String) : MemoState()
}