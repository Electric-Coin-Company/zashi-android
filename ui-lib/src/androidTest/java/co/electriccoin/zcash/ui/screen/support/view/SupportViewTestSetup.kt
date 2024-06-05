package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class SupportViewTestSetup(private val composeTestRule: ComposeContentTestRule) {
    private val onBackCount = AtomicInteger(0)

    private val onSendCount = AtomicInteger(0)

    private val onSendMessage = AtomicReference<String>(null)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getOnSendCount(): Int {
        composeTestRule.waitForIdle()
        return onSendCount.get()
    }

    fun getSendMessage(): String? {
        composeTestRule.waitForIdle()
        return onSendMessage.get()
    }

    // TODO [#1275]: Improve SupportView UI tests
    // TODO [#1275]: https://github.com/Electric-Coin-Company/zashi-android/issues/1275

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Support(
            isShowingDialog = false,
            setShowDialog = {},
            onBack = {
                onBackCount.incrementAndGet()
            },
            onSend = {
                onSendCount.incrementAndGet()
                onSendMessage.set(it)
            },
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
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
