package co.electriccoin.zcash.ui.screen.home

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import java.util.concurrent.atomic.AtomicInteger

class HomeTestSetup(
    private val composeTestRule: ComposeContentTestRule,
) {
    private val onAccountsCount = AtomicInteger(0)
    private val onSendCount = AtomicInteger(0)
    private val onReceiveCount = AtomicInteger(0)
    private val onBalancesCount = AtomicInteger(0)

    fun getOnAccountCount(): Int {
        composeTestRule.waitForIdle()
        return onAccountsCount.get()
    }

    fun getOnSendCount(): Int {
        composeTestRule.waitForIdle()
        return onSendCount.get()
    }

    fun getOnReceiveCount(): Int {
        composeTestRule.waitForIdle()
        return onReceiveCount.get()
    }

    fun getOnBalancesCount(): Int {
        composeTestRule.waitForIdle()
        return onBalancesCount.get()
    }

    // TODO [#1125]: Home screen navigation: Add integration test
    // TODO [#1125]: https://github.com/Electric-Coin-Company/zashi-android/issues/1125

    // TODO [#1126]: Home screen view: Add view test
    // TODO [#1126]: https://github.com/Electric-Coin-Company/zashi-android/issues/1126

    /*
    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Home()
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            ZcashTheme {
                DefaultContent()
            }
        }
    }
     */
}
