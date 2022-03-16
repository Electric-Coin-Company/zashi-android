package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.sdk.fixture.WalletBirthdayFixture
import cash.z.ecc.sdk.test.count
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WalletBirthdayTest {
    @Test
    @SmallTest
    fun serialize() {
        val walletBirthday = WalletBirthdayFixture.new()

        val jsonObject = walletBirthday.toJson()
        assertEquals(5, jsonObject.keys().count())
        assertTrue(jsonObject.has(WalletBirthday.KEY_VERSION))
        assertTrue(jsonObject.has(WalletBirthday.KEY_HEIGHT))
        assertTrue(jsonObject.has(WalletBirthday.KEY_HASH))
        assertTrue(jsonObject.has(WalletBirthday.KEY_EPOCH_SECONDS))
        assertTrue(jsonObject.has(WalletBirthday.KEY_TREE))

        assertEquals(1, jsonObject.getInt(WalletBirthday.KEY_VERSION))
        assertEquals(WalletBirthdayFixture.HEIGHT, jsonObject.getInt(WalletBirthday.KEY_HEIGHT))
        assertEquals(WalletBirthdayFixture.HASH, jsonObject.getString(WalletBirthday.KEY_HASH))
        assertEquals(WalletBirthdayFixture.EPOCH_SECONDS, jsonObject.getLong(WalletBirthday.KEY_EPOCH_SECONDS))
        assertEquals(WalletBirthdayFixture.TREE, jsonObject.getString(WalletBirthday.KEY_TREE))
    }

    @Test
    @SmallTest
    fun epoch_seconds_as_long_that_would_overflow_int() {
        val walletBirthday = WalletBirthdayFixture.new(time = Long.MAX_VALUE)

        val jsonObject = walletBirthday.toJson()

        assertEquals(Long.MAX_VALUE, jsonObject.getLong(WalletBirthday.KEY_EPOCH_SECONDS))

        WalletBirthday.from(jsonObject).also {
            assertEquals(Long.MAX_VALUE, it.time)
        }
    }

    @Test
    @SmallTest
    fun round_trip() {
        val walletBirthday = WalletBirthdayFixture.new()

        val deserialized = WalletBirthday.from(walletBirthday.toJson())

        assertEquals(walletBirthday, deserialized)
        assertFalse(walletBirthday === deserialized)
    }
}
