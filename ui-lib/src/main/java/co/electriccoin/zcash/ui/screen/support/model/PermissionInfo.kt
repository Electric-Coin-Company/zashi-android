package co.electriccoin.zcash.ui.screen.support.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import co.electriccoin.zcash.spackle.getPackageInfoCompatSuspend

// TODO [#1301]: Localize support text content
// TODO [#1301]: https://github.com/Electric-Coin-Company/zashi-android/issues/1301

data class PermissionInfo(
    val permissionName: String,
    val permissionStatus: PermissionStatus
) {
    fun toSupportString() =
        buildString {
            appendLine("$permissionName $permissionStatus")
        }

    companion object {
        private val permissionsOfInterest = listOf(Manifest.permission.CAMERA)

        suspend fun all(context: Context): List<PermissionInfo> {
            val myPackageInfo: PackageInfo =
                context.packageManager
                    .getPackageInfoCompatSuspend(context.packageName, PackageManager.GET_PERMISSIONS.toLong())

            return permissionsOfInterest.map { new(context, myPackageInfo, it) }
        }

        private fun new(
            context: Context,
            packageInfo: PackageInfo,
            permissionName: String
        ): PermissionInfo =
            if (isPermissionGrantedByUser(context, permissionName)) {
                PermissionInfo(permissionName, PermissionStatus.Granted)
            } else if (isPermissionGrantedByManifest(packageInfo, permissionName)) {
                PermissionInfo(permissionName, PermissionStatus.NotGrantedByUser)
            } else {
                PermissionInfo(permissionName, PermissionStatus.NotGrantedByManifest)
            }

        private fun isPermissionGrantedByUser(
            context: Context,
            permissionName: String
        ): Boolean {
            // Note: this is only checking very basic permissions
            // Some permissions, such as REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, require different checks
            return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName)
        }

        private fun isPermissionGrantedByManifest(
            packageInfo: PackageInfo,
            permissionName: String
        ): Boolean = packageInfo.requestedPermissions?.any { permissionName == it } ?: false
    }
}

fun List<PermissionInfo>.toPermissionSupportString() =
    buildString {
        if (this@toPermissionSupportString.isNotEmpty()) {
            appendLine("Permissions:")
            this@toPermissionSupportString.forEach {
                appendLine(it.toSupportString())
            }
        }
    }

enum class PermissionStatus {
    NotGrantedByManifest,
    NotGrantedByUser,
    Granted
}
