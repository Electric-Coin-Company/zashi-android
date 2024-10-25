package co.electriccoin.zcash.ui.screen.seedrecovery.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.seed.view.SeedView
import java.util.concurrent.atomic.AtomicInteger

class SeedRecoveryTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val versionInfo: VersionInfo,
) {
    private val onBirthdayCopyCount = AtomicInteger(0)

    private val onCompleteCallbackCount = AtomicInteger(0)

    private val onBackCount = AtomicInteger(0)

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
            SeedView(
                PersistableWalletFixture.new(),
                onBack = { onBackCount.incrementAndGet() },
                onBirthdayCopy = { onBirthdayCopyCount.incrementAndGet() },
                onDone = { onCompleteCallbackCount.incrementAndGet() },
                onSeedCopy = { /* Not tested - debug mode feature only */ },
                topAppBarSubTitleState = TopAppBarSubTitleState.None,
                versionInfo = versionInfo,
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            DefaultContent()
        }
    }
}
