package co.electriccoin.zcash.ui.screen.support.model

import android.os.Build

class DeviceInfo(val manufacturer: String, val device: String, val model: String, val usableStorage: String) {

    fun toSupportString() = buildString {
        appendLine("Device: $manufacturer $device $model,")
        appendLine("Usable storage: $usableStorage,")
    }

    companion object {
        fun new(usableStorage: String) = DeviceInfo(Build.MANUFACTURER, Build.DEVICE, Build.MODEL, usableStorage)
    }
}
