package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.backup.TestBackupActivity
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BackupIntegrationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestBackupActivity>()

    private fun newTestSetup(initialStage: BackupStage): BackupTestSetup {
        return BackupTestSetup(
            composeTestRule,
            initialStage,
            TestChoicesFixture.new(TestChoicesFixture.INITIAL_CHOICES)
        )
    }

    /**
     * The test semantics are built upon StateRestorationTester component. We simulate screen state
     * restoration with method emulateSavedInstanceStateRestore(), which needs to have setContent()
     * method called beforehand. Then, after state restores after emulateSavedInstanceStateRestore(),
     * setContent() callback is called again.
     */
    @Test
    @MediumTest
    fun current_stage_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(BackupStage.EducationOverview)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(BackupStage.EducationOverview, testSetup.getStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_button)).also {
            it.performClick()
        }

        assertEquals(BackupStage.EducationRecoveryPhrase, testSetup.getStage())

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(BackupStage.EducationRecoveryPhrase, testSetup.getStage())
    }

    @Test
    @MediumTest
    fun selected_choices_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(BackupStage.Test)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())
        assertEquals(0, testSetup.getOnChoicesCallbackCount())
        assertEquals(TestChoicesFixture.INITIAL_CHOICES.size, testSetup.getSelectedChoicesCount())

        composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()
            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()
        }

        assertEquals(BackupStage.Test, testSetup.getStage())
        assertEquals(2, testSetup.getOnChoicesCallbackCount())
        assertEquals(4, testSetup.getSelectedChoicesCount())

        restorationTester.emulateSavedInstanceStateRestore()

        // we test here that the stage and the selected choices count remained unchanged after restore
        assertEquals(BackupStage.Test, testSetup.getStage())
        assertEquals(2, testSetup.getOnChoicesCallbackCount())
        assertEquals(4, testSetup.getSelectedChoicesCount())
    }

    @Test
    @MediumTest
    fun current_stage_restoration_activity() {
        val testSetup = newTestSetup(BackupStage.EducationOverview)
        testSetup.setDefaultContent()

        assertEquals(BackupStage.EducationOverview, testSetup.getStage())

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_button)).also {
            it.performClick()
        }

        assertEquals(BackupStage.EducationRecoveryPhrase, testSetup.getStage())

        composeTestRule.activityRule.scenario.onActivity {
            it.recreate()
        }

        assertEquals(BackupStage.EducationRecoveryPhrase, testSetup.getStage())
    }
}
