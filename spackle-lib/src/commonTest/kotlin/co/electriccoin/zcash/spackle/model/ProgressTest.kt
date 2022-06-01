package co.electriccoin.zcash.spackle.model

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ProgressTest {

    @Test
    fun last_greater_than_zero() {
        assertFailsWith(IllegalArgumentException::class) {
            Progress(current = Index(0), last = Index(0))
        }
    }

    @Test
    fun last_greater_or_equal_to_current() {
        assertFailsWith(IllegalArgumentException::class) {
            Progress(current = Index(5), last = Index(4))
        }
    }
}
