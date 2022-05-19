package co.electriccoin.zcash.ui.screen.scan.view

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.scan.ScanTag
import co.electriccoin.zcash.ui.screen.scan.TestScanActivity
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// TODO [#313]: https://github.com/zcash/secant-android-wallet/issues/313
class ScanPermissionGrantedViewTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestScanActivity>()

    // To automatically have CAMERA permission granted for all test in the class. Note, there is no
    // way to revoke the granted permission after it's granted.
    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    @MediumTest
    fun back() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBackCount())

        composeTestRule.clickBack()

        assertEquals(1, testSetup.getOnBackCount())
    }

    @Test
    @MediumTest
    fun check_all_ui_elements_displayed() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(getStringResource(R.string.scan_header)).also {
            it.assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag(ScanTag.CAMERA_VIEW).also {
            it.assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag(ScanTag.QR_FRAME).also {
            it.assertIsDisplayed()
        }
        composeTestRule.onNodeWithTag(ScanTag.TEXT_STATE).also {
            it.assertIsDisplayed()
        }
    }

    private fun newTestSetup() = ScanViewTestSetup(composeTestRule).apply {
        setDefaultContent()
    }

    private fun ComposeContentTestRule.clickBack() {
        onNodeWithContentDescription(getStringResource(R.string.scan_back_content_description)).also {
            it.performClick()
        }
    }
}
