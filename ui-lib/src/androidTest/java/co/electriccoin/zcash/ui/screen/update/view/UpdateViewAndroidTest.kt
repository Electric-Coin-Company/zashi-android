package co.electriccoin.zcash.ui.screen.update.view

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Rule
import org.junit.Test

// Non-multiplatform tests that require interacting with the Android system (e.g. system back navigation)
// These don't have persistent state, so they are still unit tests.
class UpdateViewAndroidTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun newTestSetup(updateInfo: UpdateInfo) =
        UpdateViewAndroidTestSetup(
            updateInfo,
            composeTestRule
        ).apply {
            setDefaultContent()
        }

    @Test
    @MediumTest
    fun postpone_optional_update_test() {
        val updateInfo =
            UpdateInfoFixture.new(
                priority = AppUpdateChecker.Priority.LOW,
                force = false,
                appUpdateInfo = null,
                state = UpdateState.Prepared
            )
        newTestSetup(updateInfo)

        composeTestRule.onNodeWithText(getStringResource(R.string.update_header), ignoreCase = true).also {
            it.assertExists()
        }

        Espresso.pressBack()

        composeTestRule.onNodeWithText(getStringResource(R.string.update_header), ignoreCase = true).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun postpone_force_update_test() {
        val updateInfo =
            UpdateInfoFixture.new(
                priority = AppUpdateChecker.Priority.HIGH,
                force = true,
                appUpdateInfo = null,
                state = UpdateState.Prepared
            )
        newTestSetup(updateInfo)

        composeTestRule.onNodeWithText(getStringResource(R.string.update_critical_header), ignoreCase = true).also {
            it.assertExists()
        }

        Espresso.pressBack()

        composeTestRule.onNodeWithText(getStringResource(R.string.update_critical_header), ignoreCase = true).also {
            it.assertExists()
        }
    }
}
