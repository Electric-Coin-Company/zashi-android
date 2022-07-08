package co.electriccoin.zcash.ui.screen.update.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.UpdateInfoFixture
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.UpdateTag
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.model.UpdateState
import co.electriccoin.zcash.ui.test.getStringResource
import co.electriccoin.zcash.ui.test.getStringResourceWithArgs
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UpdateViewTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @MediumTest
    fun texts_force_update_test() {
        val updateInfo = UpdateInfoFixture.new(
            priority = AppUpdateChecker.Priority.HIGH,
            force = true,
            appUpdateInfo = null,
            state = UpdateState.Prepared
        )

        newTestSetup(updateInfo)

        composeTestRule.onNodeWithText(
            getStringResourceWithArgs(R.string.update_critical_header)
        ).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(
            getStringResourceWithArgs(R.string.update_later_disabled_button)
        ).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun later_btn_not_force_update_test() {
        val updateInfo = UpdateInfoFixture.new(
            priority = AppUpdateChecker.Priority.LOW,
            force = false,
            appUpdateInfo = null,
            state = UpdateState.Prepared
        )
        val testSetup = newTestSetup(updateInfo)

        assertEquals(0, testSetup.getOnLaterCount())

        composeTestRule.clickLater()

        assertEquals(1, testSetup.getOnLaterCount())
    }

    @Test
    @MediumTest
    fun texts_not_force_update_test() {
        val updateInfo = UpdateInfoFixture.new(
            priority = AppUpdateChecker.Priority.MEDIUM,
            force = false,
            appUpdateInfo = null,
            state = UpdateState.Prepared
        )

        newTestSetup(updateInfo)

        composeTestRule.onNodeWithText(getStringResourceWithArgs(R.string.update_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(
            getStringResourceWithArgs(R.string.update_later_enabled_button)
        ).also {
            it.assertExists()
        }
    }

    @Test
    @MediumTest
    fun later_btn_force_update_test() {
        val updateInfo = UpdateInfoFixture.new(
            priority = AppUpdateChecker.Priority.HIGH,
            force = true,
            appUpdateInfo = null,
            state = UpdateState.Prepared
        )
        val testSetup = newTestSetup(updateInfo)

        assertEquals(0, testSetup.getOnLaterCount())

        composeTestRule.clickLater()

        assertEquals(0, testSetup.getOnLaterCount())

        Espresso.pressBack()

        assertEquals(0, testSetup.getOnLaterCount())
    }

    @Test
    @MediumTest
    fun download_btn_test() {
        val updateInfo = UpdateInfoFixture.new(appUpdateInfo = null)

        val testSetup = newTestSetup(updateInfo)

        assertEquals(0, testSetup.getOnDownloadCount())

        composeTestRule.onNodeWithText(UpdateTag.PROGRESSBAR_DOWNLOADING).also {
            it.assertDoesNotExist()
        }

        composeTestRule.clickDownload()

        assertEquals(1, testSetup.getOnDownloadCount())
    }

    @Test
    @MediumTest
    fun play_store_ref_test() {
        val updateInfo = UpdateInfoFixture.new(appUpdateInfo = null)

        val testSetup = newTestSetup(updateInfo)

        assertEquals(0, testSetup.getOnReferenceCount())
        composeTestRule.onRoot().assertExists()

        composeTestRule.onNodeWithText(getStringResource(R.string.update_link_text)).also {
            it.assertExists()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnReferenceCount())
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

private fun ComposeContentTestRule.clickDownload() {
    onNodeWithTag(UpdateTag.BTN_DOWNLOAD).also {
        it.performClick()
    }
}
