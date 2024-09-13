package co.electriccoin.zcash.spackle

import android.annotation.TargetApi
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun PackageManager.getPackageInfoCompat(
    packageName: String,
    flags: Long
): PackageInfo =
    if (AndroidApiVersion.isAtLeastTiramisu) {
        getPackageInfoTPlus(packageName, flags)
    } else {
        getPackageInfoLegacy(packageName, flags)
    }

suspend fun PackageManager.getPackageInfoCompatSuspend(
    packageName: String,
    flags: Long
): PackageInfo =
    if (AndroidApiVersion.isAtLeastTiramisu) {
        withContext(Dispatchers.IO) { getPackageInfoTPlus(packageName, flags) }
    } else {
        withContext(Dispatchers.IO) { getPackageInfoLegacy(packageName, flags) }
    }

@TargetApi(Build.VERSION_CODES.TIRAMISU)
private fun PackageManager.getPackageInfoTPlus(
    packageName: String,
    flags: Long
) = getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags))

@Suppress("Deprecation")
private fun PackageManager.getPackageInfoLegacy(
    packageName: String,
    flags: Long
) = getPackageInfo(packageName, flags.toInt())
