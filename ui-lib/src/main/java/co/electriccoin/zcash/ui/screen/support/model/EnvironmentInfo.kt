package co.electriccoin.zcash.ui.screen.support.model

import android.annotation.SuppressLint
import android.content.Context
import cash.z.ecc.sdk.ext.ui.model.MonetarySeparators
import co.electriccoin.zcash.global.StorageChecker
import co.electriccoin.zcash.spackle.AndroidApiVersion
import java.util.Locale

class EnvironmentInfo(val locale: Locale, val monetarySeparators: MonetarySeparators, val usableStorageMegabytes: Int) {

    fun toSupportString() = buildString {
        appendLine("Locale: ${locale.androidResName()}")
        appendLine("Currency grouping separator: ${monetarySeparators.grouping}")
        appendLine("Currency decimal separator: ${monetarySeparators.decimal}")
        appendLine("Usable storage: $usableStorageMegabytes MB")
    }

    companion object {
        suspend fun new(context: Context): EnvironmentInfo {
            val usableStorage = StorageChecker.checkAvailableStorageMegabytes()

            return EnvironmentInfo(currentLocale(context), MonetarySeparators.current(), usableStorage)
        }

        private fun currentLocale(context: Context) = if (AndroidApiVersion.isAtLeastN) {
            currentLocaleNPlus(context)
        } else {
            currentLocaleLegacy(context)
        }

        @SuppressLint("NewApi")
        private fun currentLocaleNPlus(context: Context) = context.resources.configuration.locales[0]

        @Suppress("Deprecation")
        private fun currentLocaleLegacy(context: Context) = context.resources.configuration.locale
    }
}

private fun Locale.androidResName() = "$language-$country"
