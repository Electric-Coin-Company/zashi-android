package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.sdk.fixture.WalletAddressesFixture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WalletAddressesTest {
    @Test
    @SmallTest
    fun security() {
        val walletAddresses = WalletAddressesFixture.new()
        val actual = WalletAddressesFixture.new().toString()
        assertFalse(actual.contains(walletAddresses.shieldedOrchard))
        assertFalse(actual.contains(walletAddresses.shieldedSapling))
        assertFalse(actual.contains(walletAddresses.transparent))
        assertFalse(actual.contains(walletAddresses.unified))
        assertFalse(actual.contains(walletAddresses.viewingKey))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @SmallTest
    fun new() = runTest {
        val expected = WalletAddressesFixture.new()
        val actual = WalletAddresses.new(PersistableWalletFixture.new())
        assertEquals(expected, actual)
    }
}
