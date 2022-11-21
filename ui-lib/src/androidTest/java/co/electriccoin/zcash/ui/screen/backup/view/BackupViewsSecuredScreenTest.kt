package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.LocalScreenSecurity
import co.electriccoin.zcash.ui.common.ScreenSecurity
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.TestChoicesFixture
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BackupViewsSecuredScreenTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup(initialStage: BackupStage) =
        TestSetup(composeTestRule, initialStage).apply {
            setContentView()
        }

    @Test
    @MediumTest
    fun acquireScreenSecuritySeedStage() = runTest {
        val testSetup = newTestSetup(BackupStage.Seed)

        assertEquals(1, testSetup.getSecureScreenCount())
    }

    @Test
    @MediumTest
    fun acquireScreenSecurityTestStage() = runTest {
        val testSetup = newTestSetup(BackupStage.Test)

        assertEquals(1, testSetup.getSecureScreenCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule, initialStage: BackupStage) {
        private val screenSecurity = ScreenSecurity()
        private val state = BackupState(initialStage)

        fun getSecureScreenCount() = screenSecurity.referenceCount.value

        fun setContentView() {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalScreenSecurity provides screenSecurity) {
                    ZcashTheme {
                        BackupWallet(
                            PersistableWalletFixture.new(),
                            state,
                            TestChoicesFixture.new(mutableMapOf()),
                            onCopyToClipboard = { },
                            onComplete = { },
                            onChoicesChanged = { }
                        )
                    }
                }
            }
        }
    }
}
