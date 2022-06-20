package co.electriccoin.zcash.ui.screen.update.integration

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.UiTestingActivity
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.UpdateTag
import co.electriccoin.zcash.ui.screen.update.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.screen.update.view.UpdateViewTestSetup
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UpdateActivityViewTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<UiTestingActivity>()

    @Test
    @MediumTest
    fun later_btn_force_update_test() {
        val updateInfo = UpdateInfoFixture.new(
            priority = AppUpdateChecker.Priority.HIGH,
            force = true,
            appUpdateInfo = null,
            state = UpdateState.Prepared,
        )
        val testSetup = newTestSetup(updateInfo)

        assertEquals(0, testSetup.getOnLaterCount())

        composeTestRule.clickLater()

        assertEquals(0, testSetup.getOnLaterCount())

        Espresso.pressBack()

        assertEquals(0, testSetup.getOnLaterCount())
    }

    private fun newTestSetup(updateInfo: UpdateInfo) = UpdateViewTestSetup(
        composeTestRule,
        updateInfo
    ).apply {
        setDefaultContent()
    }
}

private fun ComposeContentTestRule.clickLater() {
    onNodeWithTag(UpdateTag.BTN_LATER).also {
        it.performClick()
    }
}
