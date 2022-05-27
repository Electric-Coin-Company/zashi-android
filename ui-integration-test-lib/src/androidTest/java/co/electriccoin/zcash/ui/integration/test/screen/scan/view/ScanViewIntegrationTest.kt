package co.electriccoin.zcash.ui.integration.test.screen.scan.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.filters.LargeTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.integration.test.getPermissionPositiveButtonUiObject
import co.electriccoin.zcash.ui.integration.test.screen.scan.TestScanActivity
import co.electriccoin.zcash.ui.integration.test.waitForDeviceIdle
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class ScanViewIntegrationTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestScanActivity>()

    private lateinit var testSetup: ScanViewTestSetup

    @Before
    fun prepare_test_setup() {
        testSetup = ScanViewTestSetup(composeTestRule)
    }

    @Test
    @LargeTest
    fun scan_state_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(testSetup.getScanState(), ScanState.Permission)

        testSetup.grantPermission()

        assertEquals(testSetup.getScanState(), ScanState.Scanning)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(testSetup.getScanState(), ScanState.Scanning)
    }

    @Test
    @LargeTest
    fun scan_permission_dialog_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        val permissionPositiveButtonUiObject = getPermissionPositiveButtonUiObject()

        // permission dialog displayed
        Assert.assertNotNull(permissionPositiveButtonUiObject)
        Assert.assertTrue(permissionPositiveButtonUiObject!!.exists())

        restorationTester.emulateSavedInstanceStateRestore()

        // permission dialog still exists
        Assert.assertNotNull(permissionPositiveButtonUiObject)
        Assert.assertTrue(permissionPositiveButtonUiObject.exists())

        testSetup.denyPermission()
    }

    @Test
    @LargeTest
    fun scan_views_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        testSetup.grantPermission()

        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertIsDisplayed()
        }

        restorationTester.emulateSavedInstanceStateRestore()

        // scan frame and camera view are still displayed
        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertIsDisplayed()
        }

        // we need to actively wait for the camera preview initialization
        waitForDeviceIdle(timeoutMillis = 5000.milliseconds)

        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertIsDisplayed()
        }
    }
}
