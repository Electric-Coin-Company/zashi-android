package co.electriccoin.zcash.ui.common.model

import android.content.Context
import android.content.pm.ApplicationInfo
import cash.z.ecc.android.sdk.model.Locale
import co.electriccoin.zcash.build.gitCommitCount
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.build.releaseNotesEn
import co.electriccoin.zcash.build.releaseNotesEs
import co.electriccoin.zcash.spackle.EmulatorWtfUtil
import co.electriccoin.zcash.spackle.FirebaseTestLabUtil
import co.electriccoin.zcash.spackle.getPackageInfoCompat
import co.electriccoin.zcash.spackle.versionCodeCompat
import co.electriccoin.zcash.ui.BuildConfig

data class VersionInfo(
    val gitSha: String,
    val gitCommitCount: Long,
    val changelog: Changelog,
    val isDebuggable: Boolean,
    val isRunningUnderTestService: Boolean,
    val isTestnet: Boolean,
    val versionCode: Long,
    val versionName: String,
    val distributionDimension: DistributionDimension
) {
    companion object {
        fun new(context: Context): VersionInfo {
            val packageInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0L)
            val applicationInfo = context.applicationInfo

            return VersionInfo(
                // Should only be null during tests
                versionName = packageInfo.versionName?.let { "$it (${packageInfo.versionCodeCompat})" } ?: "null",
                // Should only be 0 during tests
                versionCode = packageInfo.versionCodeCompat,
                isDebuggable = (0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE),
                isRunningUnderTestService = (
                    FirebaseTestLabUtil.isFirebaseTestLab(context.applicationContext) ||
                        EmulatorWtfUtil.isEmulatorWtf(context.applicationContext)
                ),
                isTestnet = context.resources.getBoolean(cash.z.ecc.sdk.ext.R.bool.zcash_is_testnet),
                gitSha = gitSha,
                gitCommitCount = gitCommitCount.toLong(),
                changelog = Changelog.new(json = resolveBestReleaseNotes()),
                distributionDimension =
                    DistributionDimension.entries
                        .first { it.value == BuildConfig.FLAVOR_distribution }
            )
        }

        private fun resolveBestReleaseNotes(): String =
            if (Locale.getDefault().language.contains("es", ignoreCase = true)) {
                releaseNotesEs
            } else {
                releaseNotesEn
            }
    }
}

enum class DistributionDimension(
    val value: String
) {
    STORE("store"),
    FOSS("foss")
}
