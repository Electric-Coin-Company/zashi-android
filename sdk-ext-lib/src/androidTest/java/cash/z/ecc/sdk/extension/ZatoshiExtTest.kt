package cash.z.ecc.sdk.extension

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi.Companion.ZATOSHI_PER_ZEC
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ZatoshiExtTest {
    @Test
    @SmallTest
    fun to_zec_string_full_basic_test() {
        val input = ZatoshiFixture.new(ZATOSHI_PER_ZEC)

        val output = input.toZecStringFull()

        assertEquals("1${MonetarySeparators.current().decimal}000", output)
    }

    @Test
    @SmallTest
    fun to_zec_string_full_decimals_test() {
        val input = ZatoshiFixture.new((1.12345678 * ZATOSHI_PER_ZEC).toLong())

        val output = input.toZecStringFull()

        assertEquals("1${MonetarySeparators.current().decimal}12345678", output)
    }

    @Test
    @SmallTest
    fun to_zec_string_abbreviated_suffix_no_dots_no_decimals_test() {
        val input = ZatoshiFixture.new(ZATOSHI_PER_ZEC)

        val output = input.toZecStringAbbreviated("...")

        assertEquals("1${MonetarySeparators.current().decimal}000", output.main)
        assertNotEquals("...", output.suffix)
    }

    @Test
    @SmallTest
    fun to_zec_string_abbreviated_suffix_no_dots_some_decimals_test() {
        val input = ZatoshiFixture.new((1.12345678 * ZATOSHI_PER_ZEC).toLong())

        val output = input.toZecStringAbbreviated("...")

        assertEquals("1${MonetarySeparators.current().decimal}123", output.main)
        assertNotEquals("...", output.suffix)
    }

    @Test
    @SmallTest
    fun to_zec_string_abbreviated_suffix_has_dots_test() {
        val input = ZatoshiFixture.new((0.000123456 * ZATOSHI_PER_ZEC).toLong())

        val output = input.toZecStringAbbreviated("...")

        assertEquals("0${MonetarySeparators.current().decimal}000", output.main)
        assertEquals("...", output.suffix)
    }
}
