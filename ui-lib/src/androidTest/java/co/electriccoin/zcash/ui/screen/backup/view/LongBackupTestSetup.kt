package co.electriccoin.zcash.ui.screen.backup.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.backup.model.BackupStage
import co.electriccoin.zcash.ui.screen.backup.state.BackupState
import co.electriccoin.zcash.ui.screen.backup.state.TestChoices
import java.util.concurrent.atomic.AtomicInteger

class LongBackupTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    initialStage: BackupStage,
    private val initialChoices: TestChoices
) {
    private val state = BackupState(initialStage)

    private val onCopyToClipboardCount = AtomicInteger(0)

    private val onCompleteCallbackCount = AtomicInteger(0)

    private val onChoicesCallbackCount = AtomicInteger(0)

    private val onSelectedChoicesCount = AtomicInteger(initialChoices.current.value.size)

    fun getOnCopyToClipboardCount(): Int {
        composeTestRule.waitForIdle()
        return onCopyToClipboardCount.get()
    }

    fun getOnCompleteCallbackCount(): Int {
        composeTestRule.waitForIdle()
        return onCompleteCallbackCount.get()
    }

    fun getOnChoicesCallbackCount(): Int {
        composeTestRule.waitForIdle()
        return onChoicesCallbackCount.get()
    }

    fun getSelectedChoicesCount(): Int {
        composeTestRule.waitForIdle()
        return onSelectedChoicesCount.get()
    }

    fun getStage(): BackupStage {
        composeTestRule.waitForIdle()
        return state.current.value
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ZcashTheme {
            LongNewWalletBackup(
                PersistableWalletFixture.new(),
                state,
                initialChoices,
                onCopyToClipboard = { onCopyToClipboardCount.incrementAndGet() },
                onComplete = { onCompleteCallbackCount.incrementAndGet() },
                onChoicesChanged = {
                    onChoicesCallbackCount.incrementAndGet()
                    onSelectedChoicesCount.set(it)
                }
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
