package co.electriccoin.zcash.ui.common.usecase

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class NavigateToInAppBrowserUseCase {
    operator fun invoke(activity: Activity, uri: Uri) {
        val intent = CustomTabsIntent.Builder()
            .setExitAnimations(activity, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()
        intent.launchUrl(activity, uri)
    }
}
