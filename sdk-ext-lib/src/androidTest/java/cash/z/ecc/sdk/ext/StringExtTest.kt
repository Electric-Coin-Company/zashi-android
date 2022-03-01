package cash.z.ecc.sdk.ext

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {
    @Test
    fun sizeInBytes_empty() {
        assertEquals(0, "".sizeInUtf8Bytes())
    }

    @Test
    fun sizeInBytes_one() {
        assertEquals(1, "a".sizeInUtf8Bytes())
    }

    @Test
    fun sizeInBytes_unicode() {
        assertEquals(2, "á".sizeInUtf8Bytes())
    }
}
