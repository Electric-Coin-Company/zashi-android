package co.electriccoin.zcash.ui.screen.update.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object PlayStoreUtil {

    const val PLAY_STORE_APP_URI = "market://details?id="

    const val FLAGS = Intent.FLAG_ACTIVITY_NO_HISTORY or
        Intent.FLAG_ACTIVITY_NEW_TASK or
        Intent.FLAG_ACTIVITY_MULTIPLE_TASK

    /**
     * Returns Google Play store app intent. We assume the Play store app is installed, as we use
     * In-app update API.
     *
     * @param context
     *
     * @return Intent for launching the Play Store.
     */
    internal fun newActivityIntent(context: Context): Intent {
        val packageName = context.packageName
        val storeUri = Uri.parse("$PLAY_STORE_APP_URI$packageName")
        val storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // To properly handle the Play store app backstack while navigate back to our app.
        storeIntent.addFlags(FLAGS)

        return storeIntent
    }
}
