package co.electriccoin.zcash.ui.screen.update_available.util

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlayStoreUtilTest {

    companion object {
        val PLAY_STORE_URI = PlayStoreUtil.PLAY_STORE_APP_URI +
            ApplicationProvider.getApplicationContext<Context>().packageName
        val BROWSER_PAGE_URI = PlayStoreUtil.BROWSER_PAGE_URI +
            ApplicationProvider.getApplicationContext<Context>().packageName
    }

    @Test
    @SmallTest
    fun check_intent_for_store() {
        val intent = PlayStoreUtil.newActivityIntent(ApplicationProvider.getApplicationContext())
        assertNotNull(intent)
        assertEquals(intent.action, Intent.ACTION_VIEW)
        assertContains(arrayOf(PLAY_STORE_URI, BROWSER_PAGE_URI), intent.data.toString())
        assertEquals(PlayStoreUtil.FLAGS, intent.flags)
    }
}
