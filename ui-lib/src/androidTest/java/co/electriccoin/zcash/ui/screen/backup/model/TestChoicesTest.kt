package co.electriccoin.zcash.ui.screen.backup.model

import androidx.compose.runtime.saveable.SaverScope
import androidx.test.filters.SmallTest
import co.electriccoin.zcash.spackle.model.Index
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.ext.Saver
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestChoicesTest {

    @Test
    @SmallTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun save_restore_comparison() = runTest {
        val original = TestChoicesFixture.new(
            mapOf(
                Pair(Index(0), "rib"),
                Pair(Index(1), "eye"),
                Pair(Index(2), "baz"),
                Pair(Index(3), "foo"),
            )
        )
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
