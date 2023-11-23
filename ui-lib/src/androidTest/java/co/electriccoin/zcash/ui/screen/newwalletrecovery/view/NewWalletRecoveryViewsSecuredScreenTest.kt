package co.electriccoin.zcash.ui.screen.newwalletrecovery.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.filters.MediumTest
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.test.UiTestPrerequisites
import co.electriccoin.zcash.ui.common.LocalScreenSecurity
import co.electriccoin.zcash.ui.common.ScreenSecurity
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class NewWalletRecoveryViewsSecuredScreenTest : UiTestPrerequisites() {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun newTestSetup() =
        TestSetup(composeTestRule).apply {
            setContentView()
        }

    @Test
    @MediumTest
    fun acquireScreenSecurity() = runTest {
        val testSetup = newTestSetup()

        assertEquals(1, testSetup.getSecureScreenCount())
    }

    private class TestSetup(private val composeTestRule: ComposeContentTestRule) {
        private val screenSecurity = ScreenSecurity()

        fun getSecureScreenCount() = screenSecurity.referenceCount.value

        fun setContentView() {
            composeTestRule.setContent {
                CompositionLocalProvider(LocalScreenSecurity provides screenSecurity) {
                    ZcashTheme {
                        NewWalletRecovery(
                            PersistableWalletFixture.new(),
                            onSeedCopy = {},
                            onBirthdayCopy = {},
                            onComplete = {}
                        )
                    }
                }
            }
        }
    }
}
