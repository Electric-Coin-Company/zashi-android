package co.electriccoin.zcash.util

import android.annotation.TargetApi
import android.content.pm.PackageInfo
import android.os.Build
import co.electriccoin.zcash.spackle.AndroidApiVersion

object VersionCodeCompat {
    fun getVersionCode(packageInfo: PackageInfo): Long {
        return if (AndroidApiVersion.isAtLeastP) {
            getVersionCodePPlus(packageInfo)
        } else {
            getVersionCodeLegacy(packageInfo).toLong()
        }
    }

    @Suppress("Deprecation")
    private fun getVersionCodeLegacy(packageInfo: PackageInfo) = packageInfo.versionCode

    @TargetApi(Build.VERSION_CODES.P)
    private fun getVersionCodePPlus(packageInfo: PackageInfo) = packageInfo.longVersionCode
}
