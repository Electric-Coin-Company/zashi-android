package co.electriccoin.zcash.ui.screen.backup.integration

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.UiTestingActivity
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.view.LongBackupTestSetup
import co.electriccoin.zcash.ui.test.getStringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BackupActivityTest : UiTestPrerequisites() {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<UiTestingActivity>()

    private fun newTestSetup(): LongBackupTestSetup {
        return LongBackupTestSetup(
            composeTestRule,
            BackupStage.EducationOverview,
            TestChoicesFixture.new(TestChoicesFixture.INITIAL_CHOICES)
        )
    }

    @Test
    @MediumTest
    fun current_stage_restoration_activity() {
        val testSetup = newTestSetup()
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
