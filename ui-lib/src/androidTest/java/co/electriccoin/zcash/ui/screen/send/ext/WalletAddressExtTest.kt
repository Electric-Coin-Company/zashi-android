package co.electriccoin.zcash.ui.screen.send.ext

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import co.electriccoin.zcash.ui.test.getAppContext
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class WalletAddressExtTest {
    @Test
    @SmallTest
    fun testAbbreviatedSaplingAddress() =
        runTest {
            val actual = WalletAddressFixture.sapling().abbreviated(getAppContext())

            // TODO [#248]: The expected value should probably be reversed if the locale is RTL
            // TODO [#248]: https://github.com/Electric-Coin-Company/zashi-android/issues/248
            val expected = "zs1vp7kvlqr4n9gpehzt…"

            assertEquals(expected, actual)
        }

    @Test
    @SmallTest
    fun testAbbreviatedTransparentAddress() =
        runTest {
            val actual = WalletAddressFixture.transparent().abbreviated(getAppContext())

            // TODO [#248]: The expected value should probably be reversed if the locale is RTL
            // TODO [#248]: https://github.com/Electric-Coin-Company/zashi-android/issues/248
            val expected = "t1dRJRY7GmyeykJnMH38…"

            assertEquals(expected, actual)
        }
}
