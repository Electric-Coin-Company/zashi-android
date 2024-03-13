package co.electriccoin.zcash.ui.screen.newwalletrecovery.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.test.CommonTag.WALLET_BIRTHDAY
import co.electriccoin.zcash.ui.design.component.CommonTag
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class NewWalletRecoveryViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(): NewWalletRecoveryTestSetup {
        return NewWalletRecoveryTestSetup(
            composeTestRule,
            VersionInfoFixture.new()
        ).apply {
            setDefaultContent()
        }
    }

    @Test
    @MediumTest
    fun default_ui_state_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBirthdayCopyCount())
        assertEquals(0, testSetup.getOnCompleteCount())

        composeTestRule.onNodeWithContentDescription(
            label = getStringResource(R.string.zcash_logo_content_description)
        ).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_recovery_header)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_recovery_description)).also {
            it.assertExists()
        }

        composeTestRule.onNodeWithTag(CommonTag.CHIP_LAYOUT).also {
            it.performScrollTo()
            it.assertExists()
        }

        composeTestRule.onNodeWithTag(WALLET_BIRTHDAY).also {
            it.performScrollTo()
            it.assertExists()
        }

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_recovery_button_finished).uppercase())
            .also {
                it.performScrollTo()
                it.assertExists()
            }

        assertEquals(0, testSetup.getOnBirthdayCopyCount())
        assertEquals(0, testSetup.getOnCompleteCount())
    }

    @Test
    @MediumTest
    fun copy_birthday_to_clipboard_content_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBirthdayCopyCount())

        composeTestRule.onNodeWithTag(WALLET_BIRTHDAY).also {
            it.performScrollTo()
            it.performClick()
        }

        assertEquals(1, testSetup.getOnBirthdayCopyCount())
    }

    @Test
    @MediumTest
    fun click_finish_test() {
        val testSetup = newTestSetup()

        assertEquals(0, testSetup.getOnBirthdayCopyCount())
        assertEquals(0, testSetup.getOnCompleteCount())

        composeTestRule.onNodeWithText(
            text = getStringResource(R.string.new_wallet_recovery_button_finished),
            ignoreCase = true
        ).also {
            it.performScrollTo()
            it.performClick()
        }

        assertEquals(0, testSetup.getOnBirthdayCopyCount())
        assertEquals(1, testSetup.getOnCompleteCount())
    }
}
