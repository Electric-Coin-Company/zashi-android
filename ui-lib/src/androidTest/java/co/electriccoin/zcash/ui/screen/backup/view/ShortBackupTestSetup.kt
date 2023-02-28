package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.concurrent.atomic.AtomicInteger

class ShortBackupTestSetup(
    private val composeTestRule: ComposeContentTestRule,
) {

    private val onCopyToClipboardCount = AtomicInteger(0)

    private val onCompleteCallbackCount = AtomicInteger(0)

    fun getOnCopyToClipboardCount(): Int {
        composeTestRule.waitForIdle()
        return onCopyToClipboardCount.get()
    }

    fun getOnCompleteCallbackCount(): Int {
        composeTestRule.waitForIdle()
        return onCompleteCallbackCount.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ZcashTheme {
            ShortNewWalletBackup(
                PersistableWalletFixture.new(),
                onCopyToClipboard = { onCopyToClipboardCount.incrementAndGet() },
                onComplete = { onCompleteCallbackCount.incrementAndGet() },
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
