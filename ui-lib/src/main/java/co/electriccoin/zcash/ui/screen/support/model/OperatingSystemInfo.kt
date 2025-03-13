package co.electriccoin.zcash.ui.screen.support.model

import android.os.Build
import co.electriccoin.zcash.spackle.AndroidApiVersion

// TODO [#1301]: Localize support text content
// TODO [#1301]: https://github.com/Electric-Coin-Company/zashi-android/issues/1301

data class OperatingSystemInfo(
    val sdkInt: Int,
    val isPreview: Boolean
) {
    fun toSupportString() =
        buildString {
            appendLine("Platform: Android")
            if (isPreview) {
                appendLine("System API: $sdkInt (preview)")
            } else {
                appendLine("System API: $sdkInt")
            }
        }

    companion object {
        fun new() = OperatingSystemInfo(Build.VERSION.SDK_INT, AndroidApiVersion.isPreview)
    }
}
