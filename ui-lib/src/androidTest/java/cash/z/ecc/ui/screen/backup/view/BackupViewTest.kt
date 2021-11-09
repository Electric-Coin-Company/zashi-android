package cash.z.ecc.ui.screen.backup.view

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import cash.z.ecc.ui.R
import cash.z.ecc.ui.screen.backup.BackupTags
import cash.z.ecc.ui.screen.backup.model.BackupStage
import cash.z.ecc.ui.screen.backup.state.BackupState
import cash.z.ecc.ui.screen.backup.state.TestChoices
import cash.z.ecc.ui.test.getStringResource
import cash.z.ecc.ui.theme.ZcashTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BackupViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Sanity check the TestSetup
    @Test
    @MediumTest
    fun verify_test_setup_stage_1() {
        val testSetup = newTestSetup(BackupStage.EducationOverview)

        assertEquals(BackupStage.EducationOverview, testSetup.getStage())
        assertEquals(0, testSetup.getOnCompleteCallbackCount())
    }

    @Test
    @MediumTest
    fun verify_test_setup_stage_5() {
        val testSetup = newTestSetup(BackupStage.Complete)

        assertEquals(BackupStage.Complete, testSetup.getStage())
        assertEquals(0, testSetup.getOnCompleteCallbackCount())
    }

    @Test
    @MediumTest
    fun test_pass() {
        val testSetup = newTestSetup(BackupStage.Test)

        composeTestRule.onAllNodesWithTag(BackupTags.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[3].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[2].performClick()
        }

        assertEquals(BackupStage.Complete, testSetup.getStage())
    }

    @Test
    @MediumTest
    fun test_fail() {
        val testSetup = newTestSetup(BackupStage.Test)

        composeTestRule.onAllNodesWithTag(BackupTags.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[2].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[3].performClick()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_header_ouch)))

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_4_button_retry))).performClick()

        assertEquals(BackupStage.Seed, testSetup.getStage())
    }

    @Test
    @MediumTest
    fun last_stage_click_finish() {
        val testSetup = newTestSetup(BackupStage.Complete)

        val goToWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_5_button_finished))
        goToWalletButton.performClick()

        assertEquals(1, testSetup.getOnCompleteCallbackCount())
    }

    @Test
    @MediumTest
    fun last_stage_click_back_to_seed() {
        val testSetup = newTestSetup(BackupStage.Complete)

        val newWalletButton = composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_5_button_back))
        newWalletButton.performClick()

        assertEquals(0, testSetup.getOnCompleteCallbackCount())
        assertEquals(BackupStage.Seed, testSetup.getStage())
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

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_3_button_finished)).also {
            it.performClick()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())

        composeTestRule.onAllNodesWithTag(BackupTags.DROPDOWN_CHIP).also {
            it.assertCountEquals(4)

            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[1].performClick()

            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[0].performClick()

            it[2].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[3].performClick()

            it[3].performClick()
            composeTestRule.onNode(hasTestTag(BackupTags.DROPDOWN_MENU)).onChildren()[2].performClick()
        }

        assertEquals(BackupStage.Complete, testSetup.getStage())

        composeTestRule.onNode(hasText(getStringResource(R.string.new_wallet_5_button_finished))).performClick()

        assertEquals(1, testSetup.getOnCompleteCallbackCount())
    }

    private fun newTestSetup(initalStage: BackupStage) = TestSetup(composeTestRule, initalStage)

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initalStage: BackupStage) {
        private val state = BackupState(initalStage)

        private var onCompleteCallbackCount = 0

        fun getOnCompleteCallbackCount(): Int {
            composeTestRule.waitForIdle()
            return onCompleteCallbackCount
        }

        fun getStage(): BackupStage {
            composeTestRule.waitForIdle()
            return state.current.value
        }

        init {
            composeTestRule.setContent {
                ZcashTheme {
                    BackupWallet(
                        PersistableWalletFixture.new(),
                        state,
                        TestChoices(),
                        onComplete = { onCompleteCallbackCount++ }
                    )
                }
            }
        }
    }
}
