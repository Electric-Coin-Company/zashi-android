package cash.z.ecc.ui.screen.onboarding.model

import androidx.test.filters.SmallTest
import org.junit.Test

class PercentDecimalTest {

    @Test(expected = IllegalArgumentException::class)
    @SmallTest
    fun require_greater_than_zero() {
        PercentDecimal(-1.0f)
    }

    @Test(expected = IllegalArgumentException::class)
    @SmallTest
    fun require_less_than_one() {
        PercentDecimal(1.5f)
    }
}
