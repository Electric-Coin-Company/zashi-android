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
    fun testAbbreviatedSaplingAddress() = runTest {
        val actual = WalletAddressFixture.legacySapling().abbreviated(getAppContext())

        // TODO [#248]: The expected value should probably be reversed if the locale is RTL
        // TODO [#248]: https://github.com/zcash/secant-android-wallet/issues/248
        val expected = "zs1hf…skt4u"

        assertEquals(expected, actual)
    }

    @Test
    @SmallTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testAbbreviatedTransparentAddress() = runTest {
        val actual = WalletAddressFixture.legacyTransparent().abbreviated(getAppContext())

        // TODO [#248]: The expected value should probably be reversed if the locale is RTL
        // TODO [#248]: https://github.com/zcash/secant-android-wallet/issues/248
        val expected = "t1QZM…3CSPK"

        assertEquals(expected, actual)
    }
}
