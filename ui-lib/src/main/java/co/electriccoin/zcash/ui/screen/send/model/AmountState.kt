package co.electriccoin.zcash.ui.screen.send.model

import android.content.Context
import androidx.compose.runtime.saveable.mapSaver
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.fromZecString
import co.electriccoin.zcash.spackle.Twig

sealed class AmountState(
    open val value: String,
) {
    data class Valid(
        override val value: String,
        val zatoshi: Zatoshi
    ) : AmountState(value)

    data class Invalid(
        override val value: String,
    ) : AmountState(value)

    companion object {
        fun new(
            context: Context,
            monetarySeparators: MonetarySeparators,
            value: String,
            isTransparentRecipient: Boolean
        ): AmountState {
            // Validate raw input string
            val validated =
                runCatching {
                    ZecStringExt.filterContinuous(context, monetarySeparators, value)
                }.onFailure {
                    Twig.error(it) { "Failed while filtering raw amount characters" }
                }.getOrDefault(false)

            if (!validated) {
                return Invalid(value)
            }

            // Convert the input to Zatoshi type-safe amount representation
            val zatoshi = (Zatoshi.fromZecString(context, value, monetarySeparators))

            // Note that the zero funds sending is supported for sending a memo-only shielded transaction
            return when {
                (zatoshi == null) -> Invalid(value)
                (zatoshi.value == 0L && isTransparentRecipient) -> Invalid(value)
                else -> Valid(value, zatoshi)
            }
        }

        private const val TYPE_VALID = "valid" // $NON-NLS
        private const val TYPE_INVALID = "invalid" // $NON-NLS
        private const val KEY_TYPE = "type" // $NON-NLS
        private const val KEY_VALUE = "value" // $NON-NLS
        private const val KEY_ZATOSHI = "zatoshi" // $NON-NLS

        internal val Saver
            get() =
                run {
                    mapSaver<AmountState>(
                        save = { it.toSaverMap() },
                        restore = {
                            if (it.isEmpty()) {
                                null
                            } else {
                                val amountString = (it[KEY_VALUE] as String)
                                val type = (it[KEY_TYPE] as String)
                                when (type) {
                                    TYPE_VALID -> Valid(amountString, Zatoshi(it[KEY_ZATOSHI] as Long))
                                    TYPE_INVALID -> Invalid(amountString)
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

            return saverMap
        }
    }
}
