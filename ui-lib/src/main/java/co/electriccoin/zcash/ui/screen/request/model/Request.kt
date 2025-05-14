package co.electriccoin.zcash.ui.screen.request.model

import android.content.Context
import cash.z.ecc.android.sdk.ext.convertUsdToZec
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.ext.toZecString
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.fromZecString
import cash.z.ecc.android.sdk.model.toFiatString
import cash.z.ecc.sdk.extension.floor
import cash.z.ecc.sdk.extension.toZecStringFull
import co.electriccoin.zcash.ui.screen.request.ext.convertToDouble

data class Request(
    val amountState: AmountState,
    val memoState: MemoState,
    val qrCodeState: QrCodeState,
)

data class AmountState(
    val amount: String,
    val currency: RequestCurrency,
    val isValid: Boolean?
) {
    fun toZecString(
        conversion: FiatCurrencyConversion,
    ): String =
        runCatching {
            amount.convertToDouble().convertUsdToZec(conversion.priceOfZec).toZecString()
        }.getOrElse { "" }

    fun toZecStringFloored(
        conversion: FiatCurrencyConversion,
    ): String =
        runCatching {
            amount
                .convertToDouble()
                .convertUsdToZec(conversion.priceOfZec)
                .convertZecToZatoshi()
                .floor()
                .toZecStringFull()
        }.getOrElse { "" }

    fun toFiatString(context: Context, conversion: FiatCurrencyConversion) =
        runCatching {
            Zatoshi
                .fromZecString(
                    context = context,
                    zecString = amount,
                    locale = Locale.getDefault()
                )?.toFiatString(
                    currencyConversion = conversion,
                    locale = Locale.getDefault()
                ) ?: ""
        }.getOrElse { "" }
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
        fun new(memo: String, amount: String): MemoState {
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
