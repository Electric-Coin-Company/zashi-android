package co.electriccoin.zcash.ui.screen.scan.util

import android.content.Intent
import android.provider.Settings
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.test.getAppContext
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SettingsUtilTest {
    companion object {
        val SETTINGS_URI =
            SettingsUtil.SETTINGS_URI_PREFIX +
                getAppContext().packageName
    }

    @Test
    @SmallTest
    fun check_intent_to_settings() {
        val intent = SettingsUtil.newSettingsIntent(getAppContext().packageName)
        assertNotNull(intent)
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.action)
        assertContains(intent.categories, Intent.CATEGORY_DEFAULT)
        assertEquals(SettingsUtil.FLAGS, intent.flags)
        assertEquals(SETTINGS_URI, intent.data.toString())
    }
}
