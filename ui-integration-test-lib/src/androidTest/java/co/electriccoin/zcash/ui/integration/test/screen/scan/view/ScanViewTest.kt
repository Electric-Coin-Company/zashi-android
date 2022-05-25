package co.electriccoin.zcash.ui.integration.test.screen.scan.view

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.LargeTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.integration.test.getPermissionPositiveButtonUiObject
import co.electriccoin.zcash.ui.integration.test.getStringResource
import co.electriccoin.zcash.ui.integration.test.screen.scan.TestScanActivity
import co.electriccoin.zcash.ui.integration.test.waitForDeviceIdle
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScanViewTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestScanActivity>()

    private lateinit var testSetup: ScanViewTestSetup

    @Before
    fun prepareTestSetup() {
        testSetup = ScanViewTestSetup(composeTestRule).apply {
            setDefaultContent()
        }
    }

    @Test
    @LargeTest
    fun is_camera_permission_dialog_shown() {
        val permissionPositiveButtonUiObject = getPermissionPositiveButtonUiObject()

        // permission dialog displayed
        assertNotNull(permissionPositiveButtonUiObject)
        assertTrue(permissionPositiveButtonUiObject!!.exists())

        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertDoesNotExist()
        }

        // hide permission dialog
        permissionPositiveButtonUiObject.click()
    }

    @Test
    @LargeTest
    fun grant_camera_permission() {
        assertEquals(ScanState.Permission, testSetup.getScanState())

        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertDoesNotExist()
        }

        testSetup.grantPermission()

        composeTestRule.onNodeWithContentDescription(
            getStringResource(R.string.scan_back_content_description)
        ).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.scan_hint)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(ScanTag.TEXT_STATE).also {
            it.assertIsDisplayed()
            it.assertTextEquals(getStringResource(R.string.scan_state_scanning))
        }

        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertIsDisplayed()
        }

        assertEquals(ScanState.Scanning, testSetup.getScanState())

        // we need to wait for camera preview initialized
        waitForDeviceIdle(5000)

        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertIsDisplayed()
        }
    }

    @Test
    @LargeTest
    fun deny_camera_permission() {
        assertEquals(ScanState.Permission, testSetup.getScanState())

        testSetup.denyPermission()

        assertEquals(ScanState.Permission, testSetup.getScanState())

        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertDoesNotExist()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.scan_hint)).also {
            it.assertIsDisplayed()
        }

        composeTestRule.onNodeWithTag(ScanTag.TEXT_STATE).also {
            it.assertIsDisplayed()
            it.assertTextEquals(getStringResource(R.string.scan_state_permission))
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.scan_settings_button)).also {
            it.assertIsDisplayed()
            it.assertHasClickAction()
        }
    }

    @Test
    @LargeTest
    fun open_settings_test() {
        testSetup.denyPermission()

        assertEquals(0, testSetup.getOnOpenSettingsCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.scan_settings_button)).also {
            it.performClick()
        }

        assertEquals(1, testSetup.getOnOpenSettingsCount())
    }
}
