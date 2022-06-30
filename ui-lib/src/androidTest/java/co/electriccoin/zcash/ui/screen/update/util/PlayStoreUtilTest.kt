package co.electriccoin.zcash.ui.screen.update.util

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlin.test.assertContains

class PlayStoreUtilTest {

    companion object {
        val PLAY_STORE_URI = PlayStoreUtil.PLAY_STORE_APP_URI +
            ApplicationProvider.getApplicationContext<Context>().packageName
    }

    @Test
    @SmallTest
    fun check_intent_for_store() {
        val intent = PlayStoreUtil.newActivityIntent(ApplicationProvider.getApplicationContext())
        assertNotNull(intent)
        assertEquals(intent.action, Intent.ACTION_VIEW)
        assertContains(PLAY_STORE_URI, intent.data.toString())
        assertEquals(PlayStoreUtil.FLAGS, intent.flags)
    }
}
