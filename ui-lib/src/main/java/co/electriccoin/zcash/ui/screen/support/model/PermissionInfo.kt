package co.electriccoin.zcash.ui.screen.support.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import co.electriccoin.zcash.util.myPackageInfo

class PermissionInfo(val permissionName: String, val permissionStatus: PermissionStatus) {
    fun toSupportString() = buildString {
        appendLine("$permissionName $permissionStatus")
    }

    companion object {
        private val permissionsOfInterest = listOf(Manifest.permission.CAMERA)

        suspend fun all(context: Context): List<PermissionInfo> {
            val myPackageInfo: PackageInfo = context.myPackageInfo(PackageManager.GET_PERMISSIONS)

            return permissionsOfInterest.map { new(context, myPackageInfo, it) }
        }

        private fun new(context: Context, packageInfo: PackageInfo, permissionName: String): PermissionInfo {
            return if (isPermissionGrantedByUser(context, permissionName)) {
                PermissionInfo(permissionName, PermissionStatus.Granted)
            } else if (isPermissionGrantedByManifest(packageInfo, permissionName)) {
                PermissionInfo(permissionName, PermissionStatus.NotGrantedByUser)
            } else {
                PermissionInfo(permissionName, PermissionStatus.NotGrantedByManifest)
            }
        }

        private fun isPermissionGrantedByUser(context: Context, permissionName: String): Boolean {
            // Note: this is only checking very basic permissions
            // Some permissions, such as REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, require different checks
            return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName)
        }

        private fun isPermissionGrantedByManifest(packageInfo: PackageInfo, permissionName: String): Boolean {
            return packageInfo.requestedPermissions?.any { permissionName == it } ?: false
        }
    }
}

fun List<PermissionInfo>.toPermissionSupportString() = buildString {
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
    Granted,
}
