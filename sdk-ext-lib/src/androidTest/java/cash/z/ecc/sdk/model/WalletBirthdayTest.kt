package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
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
        assertTrue(jsonObject.has(WalletBirthdayCompanion.KEY_VERSION))
        assertTrue(jsonObject.has(WalletBirthdayCompanion.KEY_HEIGHT))
        assertTrue(jsonObject.has(WalletBirthdayCompanion.KEY_HASH))
        assertTrue(jsonObject.has(WalletBirthdayCompanion.KEY_EPOCH_SECONDS))
        assertTrue(jsonObject.has(WalletBirthdayCompanion.KEY_TREE))

        assertEquals(1, jsonObject.getInt(WalletBirthdayCompanion.KEY_VERSION))
        assertEquals(WalletBirthdayFixture.HEIGHT, jsonObject.getInt(WalletBirthdayCompanion.KEY_HEIGHT))
        assertEquals(WalletBirthdayFixture.HASH, jsonObject.getString(WalletBirthdayCompanion.KEY_HASH))
        assertEquals(WalletBirthdayFixture.EPOCH_SECONDS, jsonObject.getLong(WalletBirthdayCompanion.KEY_EPOCH_SECONDS))
        assertEquals(WalletBirthdayFixture.TREE, jsonObject.getString(WalletBirthdayCompanion.KEY_TREE))
    }

    @Test
    @SmallTest
    fun epoch_seconds_as_long_that_would_overflow_int() {
        val walletBirthday = WalletBirthdayFixture.new(time = Long.MAX_VALUE)

        val jsonObject = walletBirthday.toJson()

        assertEquals(Long.MAX_VALUE, jsonObject.getLong(WalletBirthdayCompanion.KEY_EPOCH_SECONDS))

        WalletBirthdayCompanion.from(jsonObject).also {
            assertEquals(Long.MAX_VALUE, it.time)
        }
    }

    @Test
    @SmallTest
    fun round_trip() {
        val walletBirthday = WalletBirthdayFixture.new()

        val deserialized = WalletBirthdayCompanion.from(walletBirthday.toJson())

        assertEquals(walletBirthday, deserialized)
        assertFalse(walletBirthday === deserialized)
    }
}
