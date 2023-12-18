package co.electriccoin.zcash.ui.common.model

import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.test.getAppContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class VersionInfoTest {
    @Test
    @SmallTest
    fun sanity_check_version_info_in_testing() {
        val versionInfo = VersionInfo.new(getAppContext())

        // We expect some VersionInfo object parameters to be empty during the testing
        // isDebuggable is not tested as it's not static during UI testing in CI or locally
        assertFalse(versionInfo.isTestnet)
        assertEquals("null", versionInfo.versionName)
        assertEquals(0, versionInfo.versionCode)
        assertNotEquals(versionInfo.gitSha, "")
        assertTrue(versionInfo.gitCommitCount >= 1)
    }
}
