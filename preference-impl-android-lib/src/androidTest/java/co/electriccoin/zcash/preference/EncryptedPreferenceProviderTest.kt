package co.electriccoin.zcash.preference

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.preference.test.fixture.StringDefaultPreferenceFixture
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

// Areas that are not covered yet:
// 1. Test observer behavior
class EncryptedPreferenceProviderTest {
    /*
     * Note: This test relies on Test Orchestrator to avoid issues with multiple runs. Specifically,
     * it purges the preference file and avoids corruption due to multiple instances of the
     * EncryptedPreferenceProvider.
     */

    private var isRun = false

    @Before
    fun checkUsingOrchestrator() {
        check(!isRun) {
            "State appears to be retained between test method invocations; verify that Test Orchestrator " +
                "is enabled and then re-run the tests"
        }

        isRun = true
    }

    @Test
    @SmallTest
    fun put_and_get_string() = runBlocking {
        val expectedValue = StringDefaultPreferenceFixture.DEFAULT_VALUE + "extra"

        val preferenceProvider = new().apply {
            putString(StringDefaultPreferenceFixture.KEY, expectedValue)
        }

        assertEquals(expectedValue, StringDefaultPreferenceFixture.new().getValue(preferenceProvider))
    }

    @Test
    @SmallTest
    fun hasKey_false() = runBlocking {
        val preferenceProvider = new()

        assertFalse(preferenceProvider.hasKey(StringDefaultPreferenceFixture.new().key))
    }

    @Test
    @SmallTest
    fun put_and_check_key() = runBlocking {
        val expectedValue = StringDefaultPreferenceFixture.DEFAULT_VALUE + "extra"

        val preferenceProvider = new().apply {
            putString(StringDefaultPreferenceFixture.KEY, expectedValue)
        }

        assertTrue(preferenceProvider.hasKey(StringDefaultPreferenceFixture.new().key))
    }

    // Note: this test case relies on undocumented implementation details of SharedPreferences
    // e.g. the directory path and the fact the preferences are stored as XML
    @Test
    @SmallTest
    fun verify_no_plaintext() = runBlocking {
        val expectedValue = StringDefaultPreferenceFixture.DEFAULT_VALUE + "extra"

        new().apply {
            putString(StringDefaultPreferenceFixture.KEY, expectedValue)
        }

        val text = File(
            File(ApplicationProvider.getApplicationContext<Context>().dataDir, "shared_prefs"),
            "$FILENAME.xml"
        ).readText()

        assertFalse(text.contains(expectedValue))
        assertFalse(text.contains(StringDefaultPreferenceFixture.KEY.key))
    }

    companion object {
        private val FILENAME = "encrypted_preference_test"
        private suspend fun new() = AndroidPreferenceProvider.newEncrypted(
            ApplicationProvider.getApplicationContext(),
            FILENAME
        )
    }
}
