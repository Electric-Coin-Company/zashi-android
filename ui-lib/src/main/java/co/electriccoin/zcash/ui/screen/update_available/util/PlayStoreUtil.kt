@file:Suppress("PackageNaming")
package co.electriccoin.zcash.ui.screen.update_available.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object PlayStoreUtil {

    const val PLAY_STORE_APP_URI = "market://details?id="

    const val FLAGS = Intent.FLAG_ACTIVITY_NO_HISTORY or
        Intent.FLAG_ACTIVITY_NEW_TASK or
        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
        Intent.FLAG_ACTIVITY_MULTIPLE_TASK

    /**
     * Checks and returns Google Play store app intent, or null in case the app is not installed. We
     * assume the Play store app is installed, as we use In-app update API.
     *
     * @param context
     *
     * @return an Intent for launching the Play Store, or null in case of failure.
     */
    @Suppress("ReturnCount")
    internal fun newActivityIntent(context: Context): Intent? {
        val packageName = context.packageName
        val storeUri = Uri.parse("$PLAY_STORE_APP_URI$packageName")
        val storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // To properly handle the Play store app backstack while navigate back to our app.
        storeIntent.addFlags(FLAGS)

        // Check Play store app availability.
        if (isIntentAvailable(context, storeIntent)) {
            return storeIntent
        }

        return null
    }

    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        val info = context.packageManager.queryIntentActivities(intent, 0)
        return info.size > 0
    }
}
