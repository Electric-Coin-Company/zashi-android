package co.electriccoin.zcash.ui.screen.support.model

import android.content.Context
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import co.electriccoin.zcash.spackle.AndroidApiVersion
import java.util.Locale

class EnvironmentInfo(val locale: Locale, val monetarySeparators: MonetarySeparators) {

    fun toSupportString() = buildString {
        appendLine("Locale: ${locale.androidResName()}")
        appendLine("Currency grouping separator: ${monetarySeparators.grouping}")
        appendLine("Currency decimal separator: ${monetarySeparators.decimal}")
    }

    companion object {
        fun new(context: Context) = EnvironmentInfo(currentLocale(context), MonetarySeparators.current())

        private fun currentLocale(context: Context) = if (AndroidApiVersion.isAtLeastN) {
            currentLocaleNPlus(context)
        } else {
            currentLocaleLegacy(context)
        }

        private fun currentLocaleNPlus(context: Context) = context.resources.configuration.locales[0]

        @Suppress("Deprecation")
        private fun currentLocaleLegacy(context: Context) = context.resources.configuration.locale
    }
}

private fun Locale.androidResName() = "$language-$country"
