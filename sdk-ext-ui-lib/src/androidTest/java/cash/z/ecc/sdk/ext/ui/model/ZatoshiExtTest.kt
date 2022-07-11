package cash.z.ecc.sdk.ext.ui.model

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.ext.ui.fixture.MonetarySeparatorsFixture
import cash.z.ecc.sdk.ext.ui.toFiatString
import cash.z.ecc.sdk.fixture.CurrencyConversionFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ZatoshiExtTest {
    companion object {
        private val EN_US_SEPARATORS = MonetarySeparatorsFixture.new()
        private val CURRENCY_CONVERSION = CurrencyConversionFixture.new()
    }

    @Test
    @SmallTest
    fun zero_zatoshi_to_fiat_conversion_test() {
        val zatoshi = ZatoshiFixture.new(0L)
        val fiatString = zatoshi.toFiatString(CURRENCY_CONVERSION, EN_US_SEPARATORS)

        fiatString.also {
            assertNotNull(it)
            assertTrue(it.isNotEmpty())
            assertTrue(it.contains("0"))
            assertTrue(it.isValidNumber(EN_US_SEPARATORS))
        }
    }

    @Test
    @SmallTest
    fun regular_zatoshi_to_fiat_conversion_test() {
        val zatoshi = ZatoshiFixture.new(123_456_789L)
        val fiatString = zatoshi.toFiatString(CURRENCY_CONVERSION, EN_US_SEPARATORS)

        fiatString.also {
            assertNotNull(it)
            assertTrue(it.isNotEmpty())
            assertTrue(it.isValidNumber(EN_US_SEPARATORS))
        }
    }

    @Test
    @SmallTest
    fun rounded_zatoshi_to_fiat_conversion_test() {
        val roundedZatoshi = ZatoshiFixture.new(100_000_000L)
        val roundedCurrencyConversion = CurrencyConversionFixture.new(
            priceOfZec = 100.0
        )

        val fiatString = roundedZatoshi.toFiatString(
            roundedCurrencyConversion,
            EN_US_SEPARATORS
        )

        fiatString.also {
            assertNotNull(it)
            assertTrue(it.isNotEmpty())
            assertTrue(it.isValidNumber(EN_US_SEPARATORS))
            assertTrue("100${EN_US_SEPARATORS.decimal}00" == it)
        }
    }
}

private fun Char.isDigitOrSeparator(separators: MonetarySeparators): Boolean {
    return this.isDigit() || this == separators.decimal || this == separators.grouping
}

private fun String.isValidNumber(separators: MonetarySeparators): Boolean {
    return this.all { return it.isDigitOrSeparator(separators) }
}
