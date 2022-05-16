package co.electriccoin.zcash.ui.screen.scan.util

import android.content.Intent
import android.net.Uri
import android.provider.Settings

object SettingsUtil {

    internal fun newSettingsIntent(
        packageName: String
    ): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:$packageName")
            flags = (
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                )
        }
    }
}
