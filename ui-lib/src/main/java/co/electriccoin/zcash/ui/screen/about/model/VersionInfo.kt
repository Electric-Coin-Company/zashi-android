package co.electriccoin.zcash.ui.screen.about.model

import android.content.pm.PackageInfo
import co.electriccoin.zcash.spackle.versionCodeCompat

data class VersionInfo(val versionName: String, val versionCode: Long) {
    companion object {
        fun new(packageInfo: PackageInfo) = VersionInfo(
            packageInfo.versionName ?: "null", // Should only be null during tests
            packageInfo.versionCodeCompat
        )
    }
}
