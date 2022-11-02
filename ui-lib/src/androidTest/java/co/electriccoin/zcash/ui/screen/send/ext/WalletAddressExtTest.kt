package co.electriccoin.zcash.ui.screen.send.ext

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.WalletAddressFixture
import co.electriccoin.zcash.ui.test.getAppContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class WalletAddressExtTest {

    @Test
    @SmallTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testAbbreviated() = runTest {
        val actual = WalletAddressFixture.legacySapling().abbreviated(getAppContext())

        // TODO [#248]: The expected value should probably be reversed if the locale is RTL
        val expected = "ztest…rxnwg"

        assertEquals(expected, actual)
    }
}
