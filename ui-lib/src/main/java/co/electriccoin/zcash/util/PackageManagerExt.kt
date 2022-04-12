package co.electriccoin.zcash.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @return Current app's package info.
 */
suspend fun Context.myPackageInfo(flags: Int): PackageInfo {
    return try {
        withContext(Dispatchers.IO) { packageManager.getPackageInfo(packageName, flags) }
    } catch (e: PackageManager.NameNotFoundException) {
        // The app's own package must exist, so this should never occur.
        throw AssertionError(e)
    }
}
