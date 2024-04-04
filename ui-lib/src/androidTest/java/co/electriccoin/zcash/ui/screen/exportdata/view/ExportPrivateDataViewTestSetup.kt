package co.electriccoin.zcash.ui.screen.exportdata.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ExportPrivateDataViewTestSetup(private val composeTestRule: ComposeContentTestRule) {
    private val onBackCount = AtomicInteger(0)

    private val onAgree = AtomicBoolean(false)

    private val onConfirmCount = AtomicInteger(0)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getOnAgree(): Boolean {
        composeTestRule.waitForIdle()
        return onAgree.get()
    }

    fun getOnConfirmCount(): Int {
        composeTestRule.waitForIdle()
        return onConfirmCount.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ExportPrivateData(
            snackbarHostState = SnackbarHostState(),
            onBack = {
                onBackCount.incrementAndGet()
            },
            onAgree = {
                onAgree.getAndSet(it)
            },
            onConfirm = {
                onConfirmCount.incrementAndGet()
            },
            walletRestoringState = WalletRestoringState.NONE
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
