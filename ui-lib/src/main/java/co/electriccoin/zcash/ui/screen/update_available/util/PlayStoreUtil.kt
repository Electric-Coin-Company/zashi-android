@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object PlayStoreUtil {

    const val PLAY_STORE_APP_URI = "market://details?id="
    const val BROWSER_PAGE_URI = "https://play.google.com/store/apps/details?id="

    const val FLAGS = Intent.FLAG_ACTIVITY_NO_HISTORY or
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
        Intent.FLAG_ACTIVITY_MULTIPLE_TASK

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
        var storeUri = Uri.parse("$PLAY_STORE_APP_URI$packageName")
        var storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // To properly handle the Play Store app backstack while navigate back to our app.
        storeIntent.addFlags(FLAGS)

        // Play store app
        if (isIntentAvailable(context, storeIntent))
            return storeIntent

        storeUri = Uri.parse("$BROWSER_PAGE_URI$packageName")
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
