package co.electriccoin.zcash.ui.screen.send.model

import android.content.Context
import androidx.compose.runtime.saveable.mapSaver
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Locale
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.UserInputNumberParser
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.toFiatString
import cash.z.ecc.android.sdk.model.toKotlinLocale
import cash.z.ecc.android.sdk.model.toZatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState

sealed interface AmountState {
    val value: String
    val fiatValue: String
    val lastFieldChangedByUser: AmountField

    data class Valid(
        override val value: String,
        override val fiatValue: String,
        override val lastFieldChangedByUser: AmountField,
        val zatoshi: Zatoshi,
    ) : AmountState

    data class Invalid(
        override val value: String,
        override val fiatValue: String,
        override val lastFieldChangedByUser: AmountField
    ) : AmountState

    companion object {
        @Suppress("LongParameterList")
        fun newFromZec(
            locale: java.util.Locale,
            value: String,
            fiatValue: String,
            isTransparentOrTextRecipient: Boolean,
            exchangeRateState: ExchangeRateState,
            lastFieldChangedByUser: AmountField = AmountField.ZEC
        ): AmountState {
            val normalized = UserInputNumberParser.normalizeInput(value, locale)

            val zecAmount =
                UserInputNumberParser.toBigDecimalOrNull(normalized, locale)
                    ?: return Invalid(normalized, if (normalized.isBlank()) "" else fiatValue, lastFieldChangedByUser)

            val zatoshi = zecAmount.convertZecToZatoshi()

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
                (zatoshi.value == 0L && isTransparentOrTextRecipient) ->
                    Invalid(normalized, fiatValue, lastFieldChangedByUser)
                else -> {
                    Valid(
                        value = normalized,
                        zatoshi = zatoshi,
                        fiatValue =
                            if (currencyConversion == null) {
                                fiatValue
                            } else {
                                zatoshi.toFiatString(
                                    currencyConversion = currencyConversion,
                                    locale = locale.toKotlinLocale(),
                                )
                            },
                        lastFieldChangedByUser = lastFieldChangedByUser
                    )
                }
            }
        }

        @Suppress("LongParameterList")
        fun newFromFiat(
            locale: java.util.Locale,
            value: String,
            fiatValue: String,
            isTransparentOrTextRecipient: Boolean,
            exchangeRateState: ExchangeRateState,
        ): AmountState {
            val normalized = UserInputNumberParser.normalizeInput(fiatValue, locale)

            val fiatAmount =
                UserInputNumberParser.toBigDecimalOrNull(normalized, locale)
                    ?: return Invalid(
                        value = if (normalized.isBlank()) "" else value,
                        fiatValue = normalized,
                        lastFieldChangedByUser = AmountField.FIAT
                    )

            val zatoshi =
                (exchangeRateState as? ExchangeRateState.Data)?.currencyConversion?.toZatoshi(amount = fiatAmount)

            return when {
                zatoshi == null -> {
                    Invalid(
                        value = if (fiatValue.isBlank()) "" else value,
                        fiatValue = fiatValue,
                        lastFieldChangedByUser = AmountField.FIAT
                    )
                }
                (zatoshi.value == 0L && isTransparentOrTextRecipient) -> {
                    Invalid(
                        value = if (fiatValue.isBlank()) "" else value,
                        fiatValue = fiatValue,
                        lastFieldChangedByUser = AmountField.FIAT
                    )
                }
                else -> {
                    Valid(
                        value = zatoshi.toZecString(),
                        zatoshi = zatoshi,
                        fiatValue = normalized,
                        lastFieldChangedByUser = AmountField.FIAT
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
        private const val KEY_LAST_FIELD_CHANGED_BY_USER = "last_field_changed_by_user" // $NON-NLS

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
                                val lastFieldChangedByUser =
                                    AmountField.valueOf(it[KEY_LAST_FIELD_CHANGED_BY_USER] as String)
                                when (type) {
                                    TYPE_VALID ->
                                        Valid(
                                            value = amountString,
                                            fiatValue = fiatAmountString,
                                            zatoshi = Zatoshi(it[KEY_ZATOSHI] as Long),
                                            lastFieldChangedByUser = lastFieldChangedByUser
                                        )

                                    TYPE_INVALID ->
                                        Invalid(
                                            value = amountString,
                                            fiatValue = fiatAmountString,
                                            lastFieldChangedByUser = lastFieldChangedByUser
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
            saverMap[KEY_LAST_FIELD_CHANGED_BY_USER] = this.lastFieldChangedByUser.name

            return saverMap
        }
    }
}

enum class AmountField { ZEC, FIAT }
