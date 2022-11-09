package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.WalletAddressesFixture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletAddressesTest {
    @Test
    @SmallTest
    fun security() = runTest {
        val walletAddresses = WalletAddressesFixture.new()
        val actual = WalletAddressesFixture.new().toString()
        assertFalse(actual.contains(walletAddresses.sapling.address))
        assertFalse(actual.contains(walletAddresses.transparent.address))
        assertFalse(actual.contains(walletAddresses.unified.address))
    }
}
