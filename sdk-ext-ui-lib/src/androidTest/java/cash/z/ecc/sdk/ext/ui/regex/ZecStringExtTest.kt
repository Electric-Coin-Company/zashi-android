package cash.z.ecc.sdk.ext.ui.regex

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.ext.ui.R
import cash.z.ecc.sdk.ext.ui.ZecStringExt
import cash.z.ecc.sdk.ext.ui.fixture.MonetarySeparatorsFixture
import cash.z.ecc.sdk.ext.ui.test.getStringResourceWithArgs
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ZecStringExtTest {

    companion object {
        private val EN_US_SEPARATORS = MonetarySeparatorsFixture.new()
    }

    private fun getContinuousRegex(): Regex {
        return getStringResourceWithArgs(
            R.string.co_electriccoin_zcash_zec_amount_regex_continuous_filter,
            arrayOf(
                EN_US_SEPARATORS.grouping,
                EN_US_SEPARATORS.decimal
            )
        ).toRegex()
    }

    private fun getConfirmRegex(): Regex {
        return getStringResourceWithArgs(
            R.string.co_electriccoin_zcash_zec_amount_regex_confirm_filter,
            arrayOf(
                EN_US_SEPARATORS.grouping,
                EN_US_SEPARATORS.decimal
            )
        ).toRegex()
    }

    @Test
    @SmallTest
    fun check_continuous_regex_validity() {
        val regexString = getStringResourceWithArgs(
            R.string.co_electriccoin_zcash_zec_amount_regex_continuous_filter,
            arrayOf(
                EN_US_SEPARATORS.grouping,
                EN_US_SEPARATORS.decimal
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
    fun check_confirm_regex_validity() {
        val regexString = getStringResourceWithArgs(
            R.string.co_electriccoin_zcash_zec_amount_regex_confirm_filter,
            arrayOf(
                EN_US_SEPARATORS.grouping,
                EN_US_SEPARATORS.decimal
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
    fun check_continuous_regex_functionality_valid_inputs() {
        getContinuousRegex().also {
            assertTrue(it.matches(""))
            assertTrue(it.matches("123"))
            assertTrue(it.matches("${EN_US_SEPARATORS.decimal}"))
            assertTrue(it.matches("${EN_US_SEPARATORS.decimal}123"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.grouping}"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.grouping}456"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.decimal}"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.decimal}456"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.grouping}456${EN_US_SEPARATORS.decimal}"))
            assertTrue(it.matches("123${EN_US_SEPARATORS.grouping}456${EN_US_SEPARATORS.decimal}789"))
            assertTrue(it.matches("1${EN_US_SEPARATORS.grouping}234${EN_US_SEPARATORS.grouping}567${EN_US_SEPARATORS.decimal}00"))
        }
    }

    @Test
    @SmallTest
    fun check_continuous_regex_functionality_invalid_inputs() {
        getContinuousRegex().also {
            assertFalse(it.matches("aaa"))
            assertFalse(it.matches("123aaa"))
            assertFalse(it.matches("${EN_US_SEPARATORS.grouping}"))
            assertFalse(it.matches("${EN_US_SEPARATORS.grouping}123"))
            assertFalse(it.matches("123${EN_US_SEPARATORS.grouping}${EN_US_SEPARATORS.grouping}"))
            assertFalse(it.matches("123${EN_US_SEPARATORS.decimal}${EN_US_SEPARATORS.decimal}"))
            assertFalse(it.matches("1${EN_US_SEPARATORS.grouping}2${EN_US_SEPARATORS.grouping}3"))
            assertFalse(it.matches("1${EN_US_SEPARATORS.decimal}2${EN_US_SEPARATORS.decimal}3"))
            assertFalse(it.matches("1${EN_US_SEPARATORS.decimal}2${EN_US_SEPARATORS.grouping}3"))
        }
    }

    @Test
    @SmallTest
    fun check_confirm_regex_functionality_valid_inputs() {
        getConfirmRegex().also {
            assertTrue(it.matches("123"))
            assertTrue(it.matches(".123"))
            assertTrue(it.matches("1,234"))
            assertTrue(it.matches("1,234,567,890"))
            assertTrue(it.matches("1.2"))
            assertTrue(it.matches("123.4"))
            assertTrue(it.matches("1.234"))
            assertTrue(it.matches("1,123."))
            assertTrue(it.matches("1,234.567"))
            assertTrue(it.matches("1,234,567.890"))
        }
    }

    @Test
    @SmallTest
    fun check_confirm_regex_functionality_invalid_inputs() {
        getContinuousRegex().also {
            assertFalse(it.matches("+@#$~^&*="))
            assertFalse(it.matches("asdf"))
            assertFalse(it.matches(".."))
            assertFalse(it.matches(","))
            assertFalse(it.matches(",,"))
            assertFalse(it.matches(",."))
            assertFalse(it.matches(".,"))
            assertFalse(it.matches(",123"))
            assertFalse(it.matches("1,2,3"))
            assertFalse(it.matches("1.2,3,4"))
            assertFalse(it.matches("123,,456"))
            assertFalse(it.matches("123..456"))
            assertFalse(it.matches("1.234,567"))
            assertFalse(it.matches("1.234,567,890"))
        }
    }

    @Test
    @SmallTest
    fun check_digits_between_grouping_separators_valid_test() {
        assertTrue(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "123"))
        assertTrue(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}234"))
        assertTrue(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}234${EN_US_SEPARATORS.grouping}"))
        assertTrue(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}234${EN_US_SEPARATORS.grouping}5"))
        assertTrue(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}234${EN_US_SEPARATORS.grouping}567${EN_US_SEPARATORS.grouping}8"))
    }

    @Test
    @SmallTest
    fun check_digits_between_grouping_separators_invalid_test() {
        assertFalse(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}1${EN_US_SEPARATORS.grouping}2"))
        assertFalse(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}12${EN_US_SEPARATORS.grouping}3"))
        assertFalse(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}1234${EN_US_SEPARATORS.grouping}"))
        assertFalse(ZecStringExt.checkFor3Digits(EN_US_SEPARATORS, "1${EN_US_SEPARATORS.grouping}123${EN_US_SEPARATORS.grouping}4${EN_US_SEPARATORS.grouping}"))
    }
}
