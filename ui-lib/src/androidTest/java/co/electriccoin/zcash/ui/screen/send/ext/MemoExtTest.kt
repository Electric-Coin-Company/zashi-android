package co.electriccoin.zcash.ui.screen.send.ext

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.fixture.MemoFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getAppContext
import org.junit.Test
import kotlin.test.assertEquals

class MemoExtTest {

    @Test
    @SmallTest
    fun value_or_empty_char_test_empty_input() {
        val actual = MemoFixture.new(memoString = "").valueOrEmptyChar(getAppContext())

        val expected = getAppContext().getString(R.string.empty_char)

        assertEquals(expected, actual)
    }

    @Test
    @SmallTest
    fun value_or_empty_char_test_non_empty_input() {
        val actual = MemoFixture.new(memoString = MemoFixture.MEMO_STRING).valueOrEmptyChar(getAppContext())

        val expected = MemoFixture.MEMO_STRING

        assertEquals(expected, actual)
    }
}
