package co.electriccoin.zcash.ui.screen.support.model

import android.content.pm.PackageInfo
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.spackle.versionCodeCompat

data class AppInfo(val versionName: String, val versionCode: Long, val gitSha: String) {
    fun toSupportString() =
        buildString {
            appendLine("App version: $versionName ($versionCode) $gitSha")
        }

    companion object {
        fun new(packageInfo: PackageInfo) =
            AppInfo(
                // Should only be null during tests
                packageInfo.versionName ?: "null",
                packageInfo.versionCodeCompat,
                gitSha
            )
    }
}
