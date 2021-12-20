package cash.z.ecc.ui.screen.restore.model

import cash.z.ecc.ui.screen.restore.state.WordList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class WordListTest {
    @Test
    fun append() {
        val wordList = WordList(listOf("foo"))
        val initialList = wordList.current.value

        wordList.append(listOf("bar"))

        assertEquals(listOf("foo", "bar"), wordList.current.value)
        assertNotEquals(initialList, wordList.current.value)
    }

    @Test
    fun set() {
        val wordList = WordList(listOf("foo"))
        val initialList = wordList.current.value

        wordList.set(listOf("bar"))

        assertEquals(listOf("bar"), wordList.current.value)
        assertNotEquals(initialList, wordList.current.value)
    }
}
