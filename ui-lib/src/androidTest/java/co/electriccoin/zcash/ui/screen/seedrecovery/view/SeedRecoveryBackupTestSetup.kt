package co.electriccoin.zcash.ui.screen.seedrecovery.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.concurrent.atomic.AtomicInteger

class SeedRecoveryBackupTestSetup(
    private val composeTestRule: ComposeContentTestRule,
) {

    private val onSeedCopyCount = AtomicInteger(0)

    private val onBirthdayCopyCount = AtomicInteger(0)

    private val onCompleteCallbackCount = AtomicInteger(0)

    private val onBackCount = AtomicInteger(0)

    fun getOnSeedCopyCount(): Int {
        composeTestRule.waitForIdle()
        return onSeedCopyCount.get()
    }
    fun getOnBirthdayCopyCount(): Int {
        composeTestRule.waitForIdle()
        return onBirthdayCopyCount.get()
    }

    fun getOnCompleteCount(): Int {
        composeTestRule.waitForIdle()
        return onCompleteCallbackCount.get()
    }

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ZcashTheme {
            SeedRecovery(
                PersistableWalletFixture.new(),
                onBack = { onBackCount.incrementAndGet() },
                onSeedCopy = { onSeedCopyCount.incrementAndGet() },
                onBirthdayCopy = { onBirthdayCopyCount.incrementAndGet() },
                onDone = { onCompleteCallbackCount.incrementAndGet() },
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
