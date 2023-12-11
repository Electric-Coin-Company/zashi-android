package co.electriccoin.zcash.ui.screen.support.model

import android.os.Build
import co.electriccoin.zcash.spackle.AndroidApiVersion

data class OperatingSystemInfo(val sdkInt: Int, val isPreview: Boolean) {
    fun toSupportString() =
        buildString {
            if (isPreview) {
                appendLine("Android API: $sdkInt (preview)")
            } else {
                appendLine("Android API: $sdkInt")
            }
        }

    companion object {
        fun new() = OperatingSystemInfo(Build.VERSION.SDK_INT, AndroidApiVersion.isPreview)
    }
}
