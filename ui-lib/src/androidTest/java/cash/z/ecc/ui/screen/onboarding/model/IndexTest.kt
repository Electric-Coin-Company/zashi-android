package cash.z.ecc.ui.screen.onboarding.model

import androidx.test.filters.SmallTest
import org.junit.Test

class IndexTest {
    @Test(expected = IllegalArgumentException::class)
    @SmallTest
    fun out_of_bounds() {
        Index(-1)
    }
}
