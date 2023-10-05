package cash.z.ecc.sdk.extension

import androidx.test.filters.SmallTest
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.PercentDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class PercentDecimalExtTest {

    @Test
    @SmallTest
    fun parse_non_zero_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(0.1234f).toPercentageWithDecimal()

        assertEquals("12${MonetarySeparators.current().decimal}34", parsed)
    }

    @Test
    @SmallTest
    fun parse_zero_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(0.0000f).toPercentageWithDecimal()

        assertEquals("0${MonetarySeparators.current().decimal}00", parsed)
    }

    @Test
    @SmallTest
    fun parse_max_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(1f).toPercentageWithDecimal()

        assertEquals("100${MonetarySeparators.current().decimal}00", parsed)
    }

    @Test
    @SmallTest
    fun parse_min_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(0f).toPercentageWithDecimal()

        assertEquals("0${MonetarySeparators.current().decimal}00", parsed)
    }

    @Test
    @SmallTest
    fun parse_round_down_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(0.11111f).toPercentageWithDecimal()

        assertEquals("11${MonetarySeparators.current().decimal}11", parsed)
    }

    @Test
    @SmallTest
    fun parse_round_up_percent_decimal_test() = runTest {
        val parsed = PercentDecimal(0.11119f).toPercentageWithDecimal()

        assertEquals("11${MonetarySeparators.current().decimal}12", parsed)
    }
}
