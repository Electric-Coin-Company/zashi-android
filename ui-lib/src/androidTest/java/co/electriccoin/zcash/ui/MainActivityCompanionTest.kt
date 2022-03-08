package co.electriccoin.zcash.ui

import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration

class MainActivityCompanionTest {

    @Test
    @SmallTest
    fun splashScreenDelayDisabled() {
        assertEquals(Duration.Companion.ZERO, MainActivity.SPLASH_SCREEN_DELAY)
    }
}
