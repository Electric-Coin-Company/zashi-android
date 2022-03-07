package co.electriccoin.zcash.ui.screen.restore.model

import androidx.test.filters.SmallTest
import cash.z.ecc.sdk.model.SeedPhrase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParseResultTest {
    companion object {
        private val SAMPLE_WORD_LIST = setOf("bar", "baz", "foo")
    }

    @Test
    @SmallTest
    fun continue_empty() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "")
        assertEquals(ParseResult.Continue, actual)
    }

    @Test
    @SmallTest
    fun continue_blank() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, " ")
        assertEquals(ParseResult.Continue, actual)
    }

    @Test
    @SmallTest
    fun add_single() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "baz")
        assertEquals(ParseResult.Add(listOf("baz")), actual)
    }

    @Test
    @SmallTest
    fun add_single_trimmed() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "foo ")
        assertEquals(ParseResult.Add(listOf("foo")), actual)
    }

    @Test
    @SmallTest
    fun add_multiple() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, SAMPLE_WORD_LIST.joinToString(SeedPhrase.DEFAULT_DELIMITER))
        assertEquals(ParseResult.Add(listOf("bar", "baz", "foo")), actual)
    }

    @Test
    @SmallTest
    fun add_security() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "foo")
        assertTrue(actual is ParseResult.Add)
        assertFalse(actual.toString().contains("foo"))
    }

    @Test
    @SmallTest
    fun autocomplete_single() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "f")
        assertEquals(ParseResult.Autocomplete(listOf("foo")), actual)
    }

    @Test
    @SmallTest
    fun autocomplete_multiple() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "ba")
        assertEquals(ParseResult.Autocomplete(listOf("bar", "baz")), actual)
    }

    @Test
    @SmallTest
    fun autocomplete_security() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "f")
        assertTrue(actual is ParseResult.Autocomplete)
        assertFalse(actual.toString().contains("foo"))
    }

    @Test
    @SmallTest
    fun warn_backwards_recursion() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "bb")
        assertEquals(ParseResult.Warn(listOf("bar", "baz")), actual)
    }

    @Test
    @SmallTest
    fun warn_backwards_recursion_2() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "bad")
        assertEquals(ParseResult.Warn(listOf("bar", "baz")), actual)
    }

    @Test
    @SmallTest
    fun warn_security() {
        val actual = ParseResult.new(SAMPLE_WORD_LIST, "foob")
        assertTrue(actual is ParseResult.Warn)
        assertFalse(actual.toString().contains("foo"))
    }
}
