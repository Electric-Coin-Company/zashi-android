package cash.z.ecc.sdk.ext.ui.model

import android.os.Build
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.ext.ui.fixture.MonetarySeparatorsFixture
import cash.z.ecc.sdk.ext.ui.toFiatCurrencyState
import cash.z.ecc.sdk.fixture.CurrencyConversionFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

class FiatCurrencyConversionRateStateTest {
    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun future_near() {
        val zatoshi = ZatoshiFixture.new()

        val frozenClock = FrozenClock(
            CurrencyConversionFixture.TIMESTAMP - FiatCurrencyConversionRateState.FUTURE_CUTOFF_AGE_INCLUSIVE
        )

        val currencyConversion = CurrencyConversionFixture.new()

        val result = zatoshi.toFiatCurrencyState(currencyConversion, MonetarySeparatorsFixture.new(), frozenClock)

        assertIs<FiatCurrencyConversionRateState.Current>(result)
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun future_far() {
        val zatoshi = ZatoshiFixture.new()

        val frozenClock = FrozenClock(
            CurrencyConversionFixture.TIMESTAMP - FiatCurrencyConversionRateState.FUTURE_CUTOFF_AGE_INCLUSIVE - 1.seconds
        )

        val currencyConversion = CurrencyConversionFixture.new()

        val result = zatoshi.toFiatCurrencyState(currencyConversion, MonetarySeparatorsFixture.new(), frozenClock)

        assertIs<FiatCurrencyConversionRateState.Unavailable>(result)
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun current() {
        val zatoshi = ZatoshiFixture.new()

        val frozenClock = FrozenClock(CurrencyConversionFixture.TIMESTAMP)

        val currencyConversion = CurrencyConversionFixture.new(
            timestamp = CurrencyConversionFixture.TIMESTAMP - 1.seconds
        )

        val result = zatoshi.toFiatCurrencyState(currencyConversion, MonetarySeparatorsFixture.new(), frozenClock)

        assertIs<FiatCurrencyConversionRateState.Current>(result)
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun stale() {
        val zatoshi = ZatoshiFixture.new()

        val frozenClock = FrozenClock(CurrencyConversionFixture.TIMESTAMP)

        val currencyConversion = CurrencyConversionFixture.new(
            timestamp = CurrencyConversionFixture.TIMESTAMP - FiatCurrencyConversionRateState.CURRENT_CUTOFF_AGE_INCLUSIVE - 1.seconds
        )

        val result = zatoshi.toFiatCurrencyState(currencyConversion, MonetarySeparatorsFixture.new(), frozenClock)

        assertIs<FiatCurrencyConversionRateState.Stale>(result)
    }

    @Test
    @SmallTest
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
    fun too_stale() {
        val zatoshi = ZatoshiFixture.new()

        val frozenClock = FrozenClock(CurrencyConversionFixture.TIMESTAMP)

        val currencyConversion = CurrencyConversionFixture.new(
            timestamp = CurrencyConversionFixture.TIMESTAMP - FiatCurrencyConversionRateState.STALE_CUTOFF_AGE_INCLUSIVE - 1.seconds
        )

        val result = zatoshi.toFiatCurrencyState(currencyConversion, MonetarySeparatorsFixture.new(), frozenClock)

        assertIs<FiatCurrencyConversionRateState.Unavailable>(result)
    }

    @Test
    @SmallTest
    fun null_conversion_rate() {
        val zatoshi = ZatoshiFixture.new()

        val result = zatoshi.toFiatCurrencyState(null, MonetarySeparatorsFixture.new())

        assertIs<FiatCurrencyConversionRateState.Unavailable>(result)
    }
}

private class FrozenClock(private val timestamp: Instant) : Clock {
    override fun now() = timestamp
}
