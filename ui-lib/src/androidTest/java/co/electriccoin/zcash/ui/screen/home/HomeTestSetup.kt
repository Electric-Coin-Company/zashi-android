package co.electriccoin.zcash.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.home.model.WalletSnapshot
import co.electriccoin.zcash.ui.screen.home.view.Home
import kotlinx.collections.immutable.persistentListOf
import java.util.concurrent.atomic.AtomicInteger

class HomeTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val walletSnapshot: WalletSnapshot,
    private val isShowFiatConversion: Boolean
) {
    private val onAboutCount = AtomicInteger(0)
    private val onSeedCount = AtomicInteger(0)
    private val onSettingsCount = AtomicInteger(0)
    private val onSupportCount = AtomicInteger(0)
    private val onReceiveCount = AtomicInteger(0)
    private val onSendCount = AtomicInteger(0)

    fun getOnAboutCount(): Int {
        composeTestRule.waitForIdle()
        return onAboutCount.get()
    }

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getOnSupportCount(): Int {
        composeTestRule.waitForIdle()
        return onSupportCount.get()
    }

    fun getOnSeedCount(): Int {
        composeTestRule.waitForIdle()
        return onSeedCount.get()
    }

    fun getOnReceiveCount(): Int {
        composeTestRule.waitForIdle()
        return onReceiveCount.get()
    }

    fun getOnSendCount(): Int {
        composeTestRule.waitForIdle()
        return onSendCount.get()
    }

    fun getWalletSnapshot(): WalletSnapshot {
        composeTestRule.waitForIdle()
        return walletSnapshot
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Home(
            walletSnapshot,
            transactionHistory = persistentListOf(),
            isUpdateAvailable = false,
            isKeepScreenOnDuringSync = false,
            isFiatConversionEnabled = isShowFiatConversion,
            isDebugMenuEnabled = false,
            goSettings = {
                onSettingsCount.incrementAndGet()
            },
            goSeedPhrase = {
                onSeedCount.incrementAndGet()
            },
            goSupport = {
                onSupportCount.incrementAndGet()
            },
            goAbout = {
                onAboutCount.incrementAndGet()
            },
            goReceive = {
                onReceiveCount.incrementAndGet()
            },
            goSend = {
                onSendCount.incrementAndGet()
            },
            resetSdk = {},
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
