package co.electriccoin.zcash.ui.screen.update_available.integration

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.update_available.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update_available.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import co.electriccoin.zcash.ui.screen.update_available.view.UpdateAvailableViewTestSetup
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UpdateAvailableViewIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun update_info_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(
            UpdateInfoFixture.new(
                priority = AppUpdateChecker.Priority.HIGH,
                force = true,
                appUpdateInfo = UpdateInfoFixture.APP_UPDATE_INFO,
                state = UpdateState.Prepared,
            )
        )

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(testSetup.getUpdateInfo().priority, AppUpdateChecker.Priority.HIGH)
        assertEquals(testSetup.getUpdateState(), UpdateState.Prepared)

        composeTestRule.onNodeWithText(getStringResource(R.string.update_available_download_button)).also {
            it.performClick()
        }

        // can be Running, Done, Canceled or Failed - depends on the Play API response
        assertNotEquals(testSetup.getUpdateState(), UpdateState.Prepared)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(testSetup.getUpdateInfo().priority, AppUpdateChecker.Priority.HIGH)
        assertNotEquals(testSetup.getUpdateState(), UpdateState.Prepared)
    }

    private fun newTestSetup(updateInfo: UpdateInfo) = UpdateAvailableViewTestSetup(
        composeTestRule,
        updateInfo
    )
}
