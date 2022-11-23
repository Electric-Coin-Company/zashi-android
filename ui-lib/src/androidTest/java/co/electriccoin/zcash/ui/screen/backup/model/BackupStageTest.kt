package co.electriccoin.zcash.ui.screen.backup.model

import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BackupStageTest {

    @Test
    @SmallTest
    fun getProgress_first() {
        val progress = BackupStage.values.first().getProgress()

        assertEquals(0, progress.current.value)
        assertEquals(6, progress.last.value)
    }

    @Test
    @SmallTest
    fun getProgress_last() {
        val progress = BackupStage.values.last().getProgress()

        assertEquals(6, progress.current.value)
        assertEquals(6, progress.last.value)
    }

    @Test
    @SmallTest
    fun hasNext_boundary() {
        val last = BackupStage.values.last()

        assertFalse(last.hasNext())
    }

    @Test
    @SmallTest
    fun hasPrevious_boundary() {
        val last = BackupStage.values.first()

        assertFalse(last.hasPrevious())
    }

    @Test
    @SmallTest
    fun getNext_from_first() {
        val first = BackupStage.values.first()
        val next = first.getNext()

        assertNotEquals(first, next)
        assertEquals(BackupStage.EducationRecoveryPhrase, next)
    }

    @Test
    @SmallTest
    fun getNext_boundary() {
        val last = BackupStage.values.last()

        assertEquals(last, last.getNext())
    }

    @Test
    @SmallTest
    fun getPrevious_from_last() {
        val last = BackupStage.values.last()
        val previous = last.getPrevious()

        assertNotEquals(last, previous)
        assertEquals(BackupStage.Complete, previous)
    }

    @Test
    @SmallTest
    fun getPrevious_boundary() {
        val first = BackupStage.values.first()

        assertEquals(first, first.getPrevious())
    }
}
