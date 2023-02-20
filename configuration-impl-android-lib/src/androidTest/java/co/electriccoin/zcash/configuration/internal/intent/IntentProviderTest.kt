package co.electriccoin.zcash.configuration.internal.intent

import android.content.Intent
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.configuration.model.entry.ConfigKey
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentProviderTest {
    @Test
    @SmallTest
    fun testInsertValue() {
        val key = ConfigKey("test")
        assertFalse(IntentConfigurationProvider.peekConfiguration().hasKey(key))

        IntentConfigurationReceiver().onReceive(
            null,
            Intent().apply {
                putExtra(ConfigurationIntent.EXTRA_STRING_KEY, key.key)
                putExtra(ConfigurationIntent.EXTRA_STRING_VALUE, "test")
            }
        )

        assertTrue(IntentConfigurationProvider.peekConfiguration().hasKey(key))
    }
}
