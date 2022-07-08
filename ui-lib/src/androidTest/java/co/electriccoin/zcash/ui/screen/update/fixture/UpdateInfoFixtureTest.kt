package co.electriccoin.zcash.ui.screen.update.fixture

import androidx.test.filters.SmallTest
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import org.junit.Test
import kotlin.test.assertEquals

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
}
