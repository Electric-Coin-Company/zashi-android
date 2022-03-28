package cash.z.ecc.sdk.ext.ui.regex

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.ext.ui.R
import cash.z.ecc.sdk.ext.ui.test.getStringResourceWithArgs
import cash.z.ecc.sdk.model.MonetarySeparators
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ZecRegexTest {

    @Test
    @SmallTest
    fun check_regex_validity() {
        val separators = MonetarySeparators.current()
        val regexString = getStringResourceWithArgs(
            R.string.zec_amount_regex_filter,
            arrayOf(
                separators.grouping,
                separators.decimal
            )
        )
        assertNotNull(regexString)

        val regexAmountChecker = regexString.toRegex()

        regexAmountChecker.also {
            assertNotNull(regexAmountChecker)
            assertTrue(regexAmountChecker.pattern.isNotEmpty())
        }
    }

    @Test
    @SmallTest
    fun check_regex_functionality_valid_inputs() {
        val separators = MonetarySeparators.current()
        val regex = getStringResourceWithArgs(
            R.string.zec_amount_regex_filter,
            arrayOf(
                separators.grouping,
                separators.decimal
            )
        ).toRegex()

        regex.also {
            assertTrue(it.matches(""))
            assertTrue(it.matches("123"))
            assertTrue(it.matches("${separators.decimal}"))
            assertTrue(it.matches("${separators.decimal}123"))
            assertTrue(it.matches("123${separators.grouping}"))
            assertTrue(it.matches("123${separators.grouping}456"))
            assertTrue(it.matches("123${separators.decimal}"))
            assertTrue(it.matches("123${separators.decimal}456"))
            assertTrue(it.matches("123${separators.grouping}456${separators.decimal}"))
            assertTrue(it.matches("123${separators.grouping}456${separators.decimal}789"))
            assertTrue(it.matches("1${separators.grouping}234${separators.grouping}567${separators.decimal}00"))
        }
    }

    @Test
    @SmallTest
    fun check_regex_functionality_invalid_inputs() {
        val separators = MonetarySeparators.current()
        val regex = getStringResourceWithArgs(
            R.string.zec_amount_regex_filter,
            arrayOf(
                separators.grouping,
                separators.decimal
            )
        ).toRegex()

        regex.also {
            assertFalse(it.matches("aaa"))
            assertFalse(it.matches("123aaa"))
            assertFalse(it.matches("${separators.grouping}"))
            assertFalse(it.matches("${separators.grouping}123"))
            assertFalse(it.matches("123${separators.grouping}${separators.grouping}"))
            assertFalse(it.matches("123${separators.decimal}${separators.decimal}"))
            assertFalse(it.matches("1${separators.grouping}2${separators.grouping}3"))
            assertFalse(it.matches("1${separators.decimal}2${separators.decimal}3"))
            assertFalse(it.matches("1${separators.decimal}2${separators.grouping}3"))
        }
    }
}
