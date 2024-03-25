package co.electriccoin.zcash.ui.screen.support.model

import android.os.Build

// TODO [#1301]: Localize support text content
// TODO [#1301]: https://github.com/Electric-Coin-Company/zashi-android/issues/1301

data class DeviceInfo(val manufacturer: String, val device: String, val model: String) {
    fun toSupportString() =
        buildString {
            appendLine("Device: $manufacturer $device $model")
        }

    companion object {
        fun new() = DeviceInfo(Build.MANUFACTURER, Build.DEVICE, Build.MODEL)
    }
}
