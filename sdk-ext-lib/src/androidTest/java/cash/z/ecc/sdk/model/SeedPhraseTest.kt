package cash.z.ecc.sdk.model

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.SeedPhraseFixture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SeedPhraseTest {
    @Test
    @SmallTest
    fun split_and_join() {
        val seedPhrase = SeedPhrase.new(SeedPhraseFixture.SEED_PHRASE)

        assertEquals(SeedPhraseFixture.SEED_PHRASE, seedPhrase.joinToString())
    }

    @Test
    @SmallTest
    fun security() {
        val seedPhrase = SeedPhraseFixture.new()
        seedPhrase.split.forEach {
            assertFalse(seedPhrase.toString().contains(it))
        }
    }
}
