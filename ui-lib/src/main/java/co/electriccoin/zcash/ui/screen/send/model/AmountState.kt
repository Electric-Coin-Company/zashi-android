package co.electriccoin.zcash.ui.screen.send.model

import android.content.Context
import androidx.compose.runtime.saveable.mapSaver
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.fromZecString
import cash.z.ecc.android.sdk.model.toFiatString
import cash.z.ecc.android.sdk.model.toZatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

sealed interface AmountState {
    val value: String
    val fiatValue: String

    data class Valid(
        override val value: String,
        override val fiatValue: String,
        val zatoshi: Zatoshi
    ) : AmountState

    data class Invalid(override val value: String, override val fiatValue: String) : AmountState

    companion object {
        @Suppress("LongParameterList")
        fun newFromZec(
            context: Context,
            monetarySeparators: MonetarySeparators,
            value: String,
            fiatValue: String,
            isTransparentOrTextRecipient: Boolean,
            exchangeRateState: ExchangeRateState,
        ): AmountState {
            val isValid = validate(context, monetarySeparators, value)

            if (!isValid) {
                return Invalid(value, if (value.isBlank()) "" else fiatValue)
            }

            val zatoshi = Zatoshi.fromZecString(context, value, Locale.getDefault())

            val currencyConversion =
                if (exchangeRateState !is ExchangeRateState.Data ||
                    (!exchangeRateState.isLoading && exchangeRateState.isStale)
                ) {
                    null
                } else {
                    exchangeRateState.currencyConversion
                }

            // Note that the zero funds sending is supported for sending a memo-only shielded transaction
            return when {
                (zatoshi == null) -> Invalid(value, if (value.isBlank()) "" else fiatValue)
                (zatoshi.value == 0L && isTransparentOrTextRecipient) -> Invalid(value, fiatValue)
                else -> {
                    Valid(
                        value = value,
                        zatoshi = zatoshi,
                        fiatValue =
                            if (currencyConversion == null) {
                                fiatValue
                            } else {
                                zatoshi.toFiatString(
                                    currencyConversion = currencyConversion,
                                    locale = Locale.getDefault(),
                                )
                            }
                    )
                }
            }
        }

        @Suppress("LongParameterList")
        fun newFromFiat(
            context: Context,
            monetarySeparators: MonetarySeparators,
            value: String,
            fiatValue: String,
            isTransparentOrTextRecipient: Boolean,
            exchangeRateState: ExchangeRateState,
        ): AmountState {
            val isValid = validate(context, monetarySeparators, fiatValue)

            if (!isValid) {
                return Invalid(value = if (fiatValue.isBlank()) "" else value, fiatValue = fiatValue)
            }

            val zatoshi =
                (exchangeRateState as? ExchangeRateState.Data)?.currencyConversion?.toZatoshi(
                    context = context,
                    value = fiatValue,
                    locale = Locale.getDefault(),
                )

            return when {
                (zatoshi == null) -> {
                    Invalid(value = if (fiatValue.isBlank()) "" else value, fiatValue = fiatValue)
                }
                (zatoshi.value == 0L && isTransparentOrTextRecipient) -> {
                    Invalid(if (fiatValue.isBlank()) "" else value, fiatValue)
                }
                else -> {
                    Valid(
                        value = zatoshi.toZecString(),
                        zatoshi = zatoshi,
                        fiatValue = fiatValue
                    )
                }
            }
        }

        private const val TYPE_VALID = "valid" // $NON-NLS
        private const val TYPE_INVALID = "invalid" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_VALUE = "value" // $NON-NLS
        private const val KEY_FIAT_VALUE = "fiat_value" // $NON-NLS
        private const val KEY_ZATOSHI = "zatoshi" // $NON-NLS

        private fun validate(
            context: Context,
            monetarySeparators: MonetarySeparators,
            value: String
        ) = runCatching {
            ZecStringExt.filterContinuous(context, monetarySeparators, value)
        }.onFailure {
            Twig.error(it) { "Failed while filtering raw amount characters" }
        }.getOrDefault(false)

        internal val Saver
            get() =
                run {
                    mapSaver(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val amountString = (it[KEY_VALUE] as String)
                                val fiatAmountString = (it[KEY_FIAT_VALUE] as String)
                                val type = (it[KEY_TYPE] as String)
                                when (type) {
                                    TYPE_VALID ->
                                        Valid(
                                            value = amountString,
                                            fiatValue = fiatAmountString,
                                            zatoshi = Zatoshi(it[KEY_ZATOSHI] as Long)
                                        )

                                    TYPE_INVALID ->
                                        Invalid(
                                            value = amountString,
                                            fiatValue = fiatAmountString
                                        )

                                    else -> null
                                }
                            }
                        }
                    )
                }

        private fun AmountState.toSaverMap(): HashMap<String, Any> {
            val saverMap = HashMap<String, Any>()
            when (this) {
                is Valid -> {
                    saverMap[KEY_TYPE] = TYPE_VALID
                    saverMap[KEY_ZATOSHI] = this.zatoshi.value
                }

                is Invalid -> saverMap[KEY_TYPE] = TYPE_INVALID
            }
            saverMap[KEY_VALUE] = this.value
            saverMap[KEY_FIAT_VALUE] = this.fiatValue

            return saverMap
        }
    }
}
