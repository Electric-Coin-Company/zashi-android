@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object PlayStoreUtil {

    /**
     * Checks and returns Google Play store app intent, an internet browser app intent, or null in
     * case of the apps not installed.
     *
     * @param context
     *
     * @return an Intent for launching the Play Store, an internet browser app, or null in case of
     * the apps are not installed.
     */
    @Suppress("ReturnCount")
    internal fun newActivityIntent(context: Context): Intent? {
        val packageName = context.packageName
        var storeUri = Uri.parse("market://details?id=$packageName")
        var storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // To properly handle the Play Store app backstack while navigate back to our app.
        storeIntent.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )

        // Play store app
        if (isIntentAvailable(context, storeIntent))
            return storeIntent

        storeUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // Browser app
        if (isIntentAvailable(context, storeIntent))
            return storeIntent

        return null
    }

    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        val info = context.packageManager.queryIntentActivities(intent, 0)
        return info.size > 0
    }
}
