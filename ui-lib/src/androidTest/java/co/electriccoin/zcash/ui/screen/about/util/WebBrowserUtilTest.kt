package co.electriccoin.zcash.ui.screen.about.util

import android.content.Intent
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertContains

class WebBrowserUtilTest {
    @Test
    @SmallTest
    fun check_intent_for_web_browser() {
        val intent = WebBrowserUtil.newActivityIntent(WebBrowserUtil.ZCASH_PRIVACY_POLICY_URI)
        assertEquals(intent.action, Intent.ACTION_VIEW)
        assertEquals(WebBrowserUtil.FLAGS, intent.flags)
        assertContains(WebBrowserUtil.ZCASH_PRIVACY_POLICY_URI, intent.data.toString())
    }
}
