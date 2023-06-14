package co.electriccoin.zcash.ui.screen.support.model

import android.content.Context
import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.global.StorageChecker
import java.util.Locale

data class EnvironmentInfo(
    val locale: Locale,
    val monetarySeparators: MonetarySeparators,
    val usableStorageMegabytes: Int
) {

    fun toSupportString() = buildString {
        appendLine("Locale: ${locale.androidResName()}")
        appendLine("Currency grouping separator: ${monetarySeparators.grouping}")
        appendLine("Currency decimal separator: ${monetarySeparators.decimal}")
        appendLine("Usable storage: $usableStorageMegabytes MB")
    }

    companion object {
        suspend fun new(context: Context): EnvironmentInfo {
            val usableStorage = StorageChecker.checkAvailableStorageMegabytes()

            return EnvironmentInfo(
                context.resources.configuration.locales[0],
                MonetarySeparators.current(),
                usableStorage
            )
        }
    }
}

private fun Locale.androidResName() = "$language-$country"
