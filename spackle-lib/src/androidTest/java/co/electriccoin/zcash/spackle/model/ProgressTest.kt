package co.electriccoin.zcash.spackle.model

import androidx.test.filters.SmallTest
import org.junit.Test

class ProgressTest {

    @Test(expected = IllegalArgumentException::class)
    @SmallTest
    fun last_greater_than_zero() {
        Progress(current = Index(0), last = Index(0))
    }

    @Test(expected = IllegalArgumentException::class)
    @SmallTest
    fun last_greater_or_equal_to_current() {
        Progress(current = Index(5), last = Index(4))
    }
}
