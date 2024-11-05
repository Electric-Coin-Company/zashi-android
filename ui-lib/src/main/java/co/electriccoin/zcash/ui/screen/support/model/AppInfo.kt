package co.electriccoin.zcash.ui.screen.support.model

import android.content.Context
import android.content.pm.PackageInfo
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.spackle.versionCodeCompat

data class AppInfo(
    val gitSha: String,
    val networkType: ZcashNetwork,
    val versionCode: Long,
    val versionName: String,
) {
    fun toSupportString() =
        buildString {
            // [versionName] contains [versionCode]
            appendLine("App version: $versionName")
            appendLine("Commit: $gitSha")
            appendLine("Network: ${networkType.networkName} (${networkType.id})")
        }

    companion object {
        fun new(
            packageInfo: PackageInfo,
            context: Context
        ) = AppInfo(
            // Should only be null during tests
            versionName = packageInfo.versionName ?: "null",
            versionCode = packageInfo.versionCodeCompat,
            gitSha = gitSha,
            networkType = ZcashNetwork.fromResources(context)
        )
    }
}
