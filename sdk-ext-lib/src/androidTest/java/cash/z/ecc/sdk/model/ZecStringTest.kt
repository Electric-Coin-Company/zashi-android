package cash.z.ecc.sdk.model

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertNull

class ZecStringTest {

    companion object {
        private val EN_US_MONETARY_SEPARATORS = MonetarySeparators(',', '.')
    }

    @Test
    fun empty_string() {
        val actual = Zatoshi.fromZecString("", EN_US_MONETARY_SEPARATORS)
        val expected = null

        assertEquals(expected, actual)
    }

    @Test
    fun decimal_monetary_separator() {
        val actual = Zatoshi.fromZecString("1.13", EN_US_MONETARY_SEPARATORS)
        val expected = Zatoshi(113000000L)

        assertEquals(expected, actual)
    }

    @Test
    fun comma_grouping_separator() {
        val actual = Zatoshi.fromZecString("1,130", EN_US_MONETARY_SEPARATORS)
        val expected = Zatoshi(113000000000L)

        assertEquals(expected, actual)
    }

    @Test
    fun decimal_monetary_and() {
        val actual = Zatoshi.fromZecString("1,130", EN_US_MONETARY_SEPARATORS)
        val expected = Zatoshi(113000000000L)

        assertEquals(expected, actual)
    }

    @Test
    fun toZecString() {
        val expected = "1.13000000"
        val actual = Zatoshi(113000000).toZecString()

        assertEquals(expected, actual)
    }

    @Test
    @Ignore("https://github.com/zcash/zcash-android-wallet-sdk/issues/412")
    fun round_trip() {
        val expected = Zatoshi(113000000L)
        val actual = Zatoshi.fromZecString(expected.toZecString(), EN_US_MONETARY_SEPARATORS)

        assertEquals(expected, actual)
    }

    @Test
    @Ignore("https://github.com/zcash/secant-android-wallet/issues/223")
    fun parse_bad_string() {
        val actual = Zatoshi.fromZecString("asdf", EN_US_MONETARY_SEPARATORS)

        assertNull(actual)
    }

    @Test
    @Ignore("https://github.com/zcash/secant-android-wallet/issues/223")
    fun parse_bad_number() {
        val actual = Zatoshi.fromZecString("1.2,3,4", EN_US_MONETARY_SEPARATORS)

        assertNull(actual)
    }
}
