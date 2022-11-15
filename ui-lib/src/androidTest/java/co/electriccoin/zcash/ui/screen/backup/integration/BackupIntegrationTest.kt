package co.electriccoin.zcash.ui.screen.backup.integration

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.BackupTag
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.view.BackupTestSetup
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class BackupIntegrationTest : UiTestPrerequisites() {

    @get:Rule
    var composeTestRule = createComposeRule()

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
    fun backup_state_education_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(BackupStage.EducationOverview)

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(BackupStage.EducationOverview.order, testSetup.getStage().order)

        composeTestRule.onNodeWithText(getStringResource(R.string.new_wallet_1_button)).also {
            it.performClick()
        }

        assertEquals(BackupStage.EducationRecoveryPhrase.order, testSetup.getStage().order)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(BackupStage.EducationRecoveryPhrase.order, testSetup.getStage().order)
    }

    @Test
    @MediumTest
    fun backup_state_test_running_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(BackupStage.Test(BackupStage.Test.TestStage.InProgress))

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(
            BackupStage.Test(BackupStage.Test.TestStage.InProgress).order,
            testSetup.getStage().order
        )

        val chipText = composeTestRule.getDropdownChipSelectedText(0, 0)

        assertNotNull(chipText)
        assertNotEquals("Chip text shouldn't be empty.", chipText, "")

        assertEquals(
            BackupStage.Test(BackupStage.Test.TestStage.InProgress).order,
            testSetup.getStage().order
        )

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(
            BackupStage.Test(BackupStage.Test.TestStage.InProgress).order,
            testSetup.getStage().order
        )

        composeTestRule.onNodeWithText(chipText).also {
            it.assertExists()
            it.assertIsDisplayed()
        }
    }

    @Test
    @MediumTest
    fun selected_choices_restoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        val testSetup = newTestSetup(BackupStage.Test(BackupStage.Test.TestStage.InProgress))

        restorationTester.setContent {
            testSetup.getDefaultContent()
        }

        assertEquals(0, testSetup.getOnChoicesCallbackCount())
        assertEquals(TestChoicesFixture.INITIAL_CHOICES.size, testSetup.getSelectedChoicesCount())

        composeTestRule.onAllNodesWithTag(BackupTag.DROPDOWN_CHIP).also {
            it[0].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[0].performClick()
            it[1].performClick()
            composeTestRule.onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[1].performClick()
        }

        assertEquals(2, testSetup.getOnChoicesCallbackCount())
        assertEquals(4, testSetup.getSelectedChoicesCount())

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(2, testSetup.getOnChoicesCallbackCount())
        assertEquals(4, testSetup.getSelectedChoicesCount())
    }
}

fun ComposeContentTestRule.getDropdownChipSelectedText(chipIndex: Int, selectionIndex: Int): String {
    return onAllNodesWithTag(BackupTag.DROPDOWN_CHIP)[chipIndex].let { chip ->
        chip.performClick()
        onNode(hasTestTag(BackupTag.DROPDOWN_MENU)).onChildren()[selectionIndex].performClick()
        chip.fetchSemanticsNode().config[SemanticsProperties.Text][selectionIndex].text
    }
}
