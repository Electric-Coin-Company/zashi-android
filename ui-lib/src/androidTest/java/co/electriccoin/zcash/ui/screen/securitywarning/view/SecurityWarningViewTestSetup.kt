package co.electriccoin.zcash.ui.screen.securitywarning.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SecurityWarningViewTestSetup(private val composeTestRule: ComposeContentTestRule) {

    private val onBackCount = AtomicInteger(0)

    private val onAcknowledged = AtomicBoolean(false)

    private val onConfirmCount = AtomicInteger(0)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getOnAcknowledged(): Boolean {
        composeTestRule.waitForIdle()
        return onAcknowledged.get()
    }

    fun getOnConfirmCount(): Int {
        composeTestRule.waitForIdle()
        return onConfirmCount.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        SecurityWarning(
            SnackbarHostState(),
            onBack = {
                onBackCount.incrementAndGet()
            },
            onPrivacyPolicy = {
                // Not tested yet. UI testing of clicking on an AnnotatedString Text part is complicated.
            },
            onAcknowledged = {
                onAcknowledged.getAndSet(it)
            },
            onConfirm = {
                onConfirmCount.incrementAndGet()
            },
            versionInfo = VersionInfoFixture.new()
        )
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            ZcashTheme {
                DefaultContent()
            }
        }
    }
}
