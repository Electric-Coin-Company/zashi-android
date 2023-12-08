package co.electriccoin.zcash.ui.screen.securitywarning.util

import android.content.Intent
import android.net.Uri

object WebBrowserUtil {
    const val FLAGS =
        Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK

    const val ZCASH_PRIVACY_POLICY_URI = "https://z.cash/privacy-policy/" // NON-NLS

    /**
     * Returns new action view app intent. We assume the a web browser app is installed.
     *
     * @param url The webpage url to open
     *
     * @return Intent for launching in a browser app.
     */
    internal fun newActivityIntent(url: String): Intent {
        val storeUri = Uri.parse(url)
        val storeIntent = Intent(Intent.ACTION_VIEW, storeUri)

        // To properly handle the browser backstack while navigate back to our app
        storeIntent.addFlags(FLAGS)

        return storeIntent
    }
}
