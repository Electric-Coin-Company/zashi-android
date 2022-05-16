package co.electriccoin.zcash.ui.screen.scan.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SettingsUtilTest {

    companion object {
        val SETTINGS_URI = SettingsUtil.SETTINGS_URI_PREFIX +
            ApplicationProvider.getApplicationContext<Context>().packageName
    }

    @Test
    @SmallTest
    fun check_intent_to_settings() {
        val intent = SettingsUtil.newSettingsIntent(ApplicationProvider.getApplicationContext<Context>().packageName)
        assertNotNull(intent)
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.action)
        assertContains(intent.categories, Intent.CATEGORY_DEFAULT)
        assertEquals(SettingsUtil.FLAGS, intent.flags)
        assertEquals(SETTINGS_URI, intent.data.toString())
    }
}
