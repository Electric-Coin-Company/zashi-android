package cash.z.ecc.ui.screen.onboarding.model

import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OnboardingStageTest {

    @Test
    @SmallTest
    fun getProgress_first() {
        val progress = OnboardingStage.values().first().getProgress()

        assertEquals(0, progress.current.value)
        assertEquals(3, progress.last.value)
    }

    @Test
    @SmallTest
    fun getProgress_last() {
        val progress = OnboardingStage.values().last().getProgress()

        assertEquals(3, progress.current.value)
        assertEquals(3, progress.last.value)
    }

    @Test
    @SmallTest
    fun hasNext_boundary() {
        val last = OnboardingStage.values().last()

        assertFalse(last.hasNext())
    }

    @Test
    @SmallTest
    fun hasPrevious_boundary() {
        val last = OnboardingStage.values().first()

        assertFalse(last.hasPrevious())
    }

    @Test
    @SmallTest
    fun getNext_from_first() {
        val first = OnboardingStage.values().first()
        val next = first.getNext()

        assertNotEquals(first, next)
        assertEquals(OnboardingStage.UnifiedAddresses, next)
    }

    @Test
    @SmallTest
    fun getNext_boundary() {
        val last = OnboardingStage.values().last()

        assertEquals(last, last.getNext())
    }

    @Test
    @SmallTest
    fun getPrevious_from_last() {
        val last = OnboardingStage.values().last()
        val previous = last.getPrevious()

        assertNotEquals(last, previous)
        assertEquals(OnboardingStage.More, previous)
    }

    @Test
    @SmallTest
    fun getPrevious_boundary() {
        val first = OnboardingStage.values().first()

        assertEquals(first, first.getPrevious())
    }
}
