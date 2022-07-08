package co.electriccoin.zcash.ui.screen.home.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import java.util.concurrent.atomic.AtomicInteger

class HomeTestSetup(
    private val composeTestRule: ComposeContentTestRule
) {
    private val onScanCount = AtomicInteger(0)
    private val onProfileCount = AtomicInteger(0)
    private val onSendCount = AtomicInteger(0)
    private val onRequestCount = AtomicInteger(0)

    fun getOnScanCount(): Int {
        composeTestRule.waitForIdle()
        return onScanCount.get()
    }

    fun getOnProfileCount(): Int {
        composeTestRule.waitForIdle()
        return onProfileCount.get()
    }

    fun getOnSendCount(): Int {
        composeTestRule.waitForIdle()
        return onSendCount.get()
    }

    fun getOnRequestCount(): Int {
        composeTestRule.waitForIdle()
        return onRequestCount.get()
    }

    @Composable
    fun getDefaultContent() {
        Home(
            WalletSnapshotFixture.new(),
            emptyList(),
            goScan = {
                onScanCount.incrementAndGet()
            },
            goProfile = {
                onProfileCount.incrementAndGet()
            },
            goSend = {
                onSendCount.incrementAndGet()
            },
            goRequest = {
                onRequestCount.incrementAndGet()
            },
            resetSdk = {},
            wipeEntireWallet = {},
            isDebugMenuEnabled = false,
            updateAvailable = false
        )
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            ZcashTheme {
                getDefaultContent()
            }
        }
    }
}
