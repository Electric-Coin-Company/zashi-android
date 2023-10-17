package co.electriccoin.zcash.ui.common.model

import android.content.Context
import android.content.pm.ApplicationInfo
import co.electriccoin.zcash.build.gitCommitCount
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.spackle.versionCodeCompat

data class VersionInfo(
    val versionName: String,
    val versionCode: Long,
    val isDebuggable: Boolean,
    val gitSha: String,
    val gitCommitCount: Long
) {
    companion object {
        fun new(context: Context): VersionInfo {
            val packageInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0L)
            val applicationInfo = context.applicationInfo

            return VersionInfo(
                versionName = packageInfo.versionName ?: "null", // Should only be null during tests
                versionCode = packageInfo.versionCodeCompat, // Should only be 0 during tests
                isDebuggable = (
                    (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) &&
                        !FirebaseTestLabUtil.isFirebaseTestLab(context.applicationContext) &&
                        !EmulatorWtfUtil.isEmulatorWtf(context.applicationContext)
                    ),
                gitSha = gitSha,
                gitCommitCount = gitCommitCount.toLong()
            )
        }
    }
}
