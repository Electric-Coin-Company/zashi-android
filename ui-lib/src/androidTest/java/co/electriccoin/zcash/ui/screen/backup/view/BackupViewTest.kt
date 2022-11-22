package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BackupViewTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(initialStage: BackupStage): BackupTestSetup {
        return BackupTestSetup(composeTestRule, initialStage, TestChoicesFixture.new(mutableMapOf())).apply {
            setDefaultContent()
        }
    }

    // Sanity check the TestSetup
    @Test
    @MediumTest
    fun verify_test_setup_stage_1() {
        val testSetup = newTestSetup(BackupStage.EducationOverview)

        assertEquals(BackupStage.EducationOverview, testSetup.getStage())
        assertEquals(0, testSetup.getOnCompleteCallbackCount())
        assertEquals(0, testSetup.getOnCopyToClipboardCount())
    }

    @Test
    @MediumTest
    fun verify_test_setup_stage_5() {
        val testSetup = newTestSetup(BackupStage.Complete)

        assertEquals(BackupStage.Complete, testSetup.getStage())
        assertEquals(0, testSetup.getOnCompleteCallbackCount())
        assertEquals(0, testSetup.getOnCopyToClipboardCount())
    }

    @Test
    @MediumTest
    fun copy_to_clipboard() {
        val testSetup = newTestSetup(BackupStage.Seed)

        composeTestRule.clickCopyToBuffer()

        assertEquals(1, testSetup.getOnCopyToClipboardCount())
    }

    @Test
    @MediumTest
    fun test_pass() {
        val testSetup = newTestSetup(BackupStage.Test)

        composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()
        }

        assertEquals(BackupStage.Complete, testSetup.getStage())
    }

    @Test
    @MediumTest
    fun test_fail() {
        val testSetup = newTestSetup(BackupStage.Test)

        composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()
        }

        assertEquals(BackupStage.Failure, testSetup.getStage())

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_header_ouch)))

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_button_retry))).also {
            it.performClick()
        }

        assertEquals(BackupStage.Seed, testSetup.getStage())

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_3_button_finished))).also {
            it.performClick()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())

        // These verify that the test itself is re-displayed

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_header))).also {
            it.assertExists()
        }

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_header_ouch))).also {
            it.assertDoesNotExist()
        }
    }

    @Test
    @MediumTest
    fun last_stage_click_finish() {
        val testSetup = newTestSetup(BackupStage.Complete)

        val goToWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_5_button_finished))
        goToWalletButton.performClick()

        assertEquals(0, testSetup.getOnCopyToClipboardCount())
        assertEquals(1, testSetup.getOnCompleteCallbackCount())
    }

    @Test
    @MediumTest
    fun complete_stage_click_back_to_seed() {
        val testSetup = newTestSetup(BackupStage.Complete)

        val newWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_5_button_back))
        newWalletButton.also {
            it.performClick()
        }

        assertEquals(0, testSetup.getOnCopyToClipboardCount())
        assertEquals(0, testSetup.getOnCompleteCallbackCount())
        assertEquals(BackupStage.ReviewSeed, testSetup.getStage())
    }

    @Test
    @MediumTest
    fun multi_stage_progression() {
        val testSetup = newTestSetup(BackupStage.EducationOverview)

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_button)).also {
            it.performClick()
        }

        assertEquals(BackupStage.EducationRecoveryPhrase, testSetup.getStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_2_button)).also {
            it.performClick()
        }

        assertEquals(BackupStage.Seed, testSetup.getStage())

        composeTestRule.clickCopyToBuffer()

        assertEquals(1, testSetup.getOnCopyToClipboardCount())

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_button_finished)).also {
            it.performClick()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())

        composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[3].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[2].performClick()
        }

        assertEquals(BackupStage.Complete, testSetup.getStage())

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_5_button_finished))).performClick()

        assertEquals(1, testSetup.getOnCompleteCallbackCount())
    }
}

fun ComposeContentTestRule.clickCopyToBuffer() {
    // open menu
    onNodeWithContentDescription(
        getStringResource(R.string.new_wallet_toolbar_more_button_content_description)
    ).also { moreMenu ->
        moreMenu.performClick()
        // click menu button
        onNodeWithText(
            getStringResource(R.string.new_wallet_3_button_copy)
        ).also { menuButton ->
            menuButton.performClick()
        }
    }
}
