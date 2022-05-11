package co.electriccoin.zcash.ui.screen.update.fixture

import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class UpdateInfoFixtureTest {

    companion object {
        val updateInfo = UpdateInfoFixture.new(appUpdateInfo = null)
    }

    @Test
    @SmallTest
    fun fixture_result_test() {
        updateInfo.also {
            assertEquals(it.priority, UpdateInfoFixture.INITIAL_PRIORITY)
            assertEquals(it.isForce, UpdateInfoFixture.INITIAL_FORCE)
            assertEquals(it.state, UpdateInfoFixture.INITIAL_STATE)
            assertEquals(it.appUpdateInfo, null)
        }
    }

    @Test
    @SmallTest
    fun fixture_copy_test() {
        val copied = updateInfo.copy(state = UpdateState.Running)
        assertNotNull(copied)
        assertNotEquals(updateInfo.state, copied.state)
        assertEquals(UpdateState.Running, copied.state)
        assertEquals(updateInfo.appUpdateInfo, copied.appUpdateInfo)
    }
}
