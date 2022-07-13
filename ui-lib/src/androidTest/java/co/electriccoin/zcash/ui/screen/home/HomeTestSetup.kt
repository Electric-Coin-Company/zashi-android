package co.electriccoin.zcash.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import co.electriccoin.zcash.ui.screen.home.view.Home
import java.util.concurrent.atomic.AtomicInteger

class HomeTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot
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

    fun getWalletSnapshot(): WalletSnapshot {
        composeTestRule.waitForIdle()
        return walletSnapshot
    }

    @Composable
    fun getDefaultContent() {
        Home(
            walletSnapshot,
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
