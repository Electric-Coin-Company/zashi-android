package co.electriccoin.zcash.ui.screen.about.util

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object WebBrowserUtil {
    const val ZCASH_PRIVACY_POLICY_URI = "https://electriccoin.co/zashi-privacy-policy/" // NON-NLS

    internal fun startActivity(
        activity: Activity,
        url: String
    ) {
        val intent =
            CustomTabsIntent
                .Builder()
                .setUrlBarHidingEnabled(true)
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .build()
        intent.launchUrl(activity, Uri.parse(url))
    }
}
