package cash.z.ecc.ui

import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration

class MainActivityTest {

    @Test
    @SmallTest
    fun splashScreenDelayDisabled() {
        assertEquals(Duration.Companion.ZERO, MainActivity.SPLASH_SCREEN_DELAY)
    }
}
