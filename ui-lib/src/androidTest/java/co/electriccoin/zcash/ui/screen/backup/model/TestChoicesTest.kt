package co.electriccoin.zcash.ui.screen.backup.model

import androidx.compose.runtime.saveable.SaverScope
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.ext.Saver
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestChoicesTest {

    @Test
    @SmallTest
    fun save_restore_comparison() {
        val original = TestChoicesFixture.new(TestChoicesFixture.INITIAL_CHOICES)
        val saved = with(TestChoices.Saver) {
            val allowingScope = SaverScope { true }
            allowingScope.save(original)
        }

        val restored = TestChoices.Saver.restore(saved!!)

        assertNotNull(restored)
        assertTrue(restored.current.value.isNotEmpty())
        assertEquals(restored.current.value.size, original.current.value.size)
        assertEquals(restored.current.value[Index(0)], original.current.value[Index(0)])
        assertEquals(restored.current.value[Index(3)], original.current.value[Index(3)])
    }

    @Test
    @SmallTest
    fun restore_empty() {
        val restored = TestChoices.Saver.restore(emptyList<Any?>())
        assertNotNull(restored)
        assertEquals(restored.current.value.size, 0)
    }
}
