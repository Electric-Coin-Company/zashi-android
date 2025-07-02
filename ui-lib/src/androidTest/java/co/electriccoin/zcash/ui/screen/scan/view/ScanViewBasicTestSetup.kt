package co.electriccoin.zcash.ui.screen.scan.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.ScanView
import co.electriccoin.zcash.ui.screen.scan.ScanScreenState
import co.electriccoin.zcash.ui.screen.scan.ScanValidationState
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class ScanViewBasicTestSetup(
    private val composeTestRule: ComposeContentTestRule
) {
    private val onBackCount = AtomicInteger(0)
    private val scanState = AtomicReference(ScanScreenState.Permission)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getScanState(): ScanScreenState {
        composeTestRule.waitForIdle()
        return scanState.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        ScanView(
            snackbarHostState = SnackbarHostState(),
            onBack = {
                onBackCount.incrementAndGet()
            },
            onScan = {},
            onScanError = {},
            onOpenSettings = {},
            onScanStateChange = {
                scanState.set(it)
            },
            validationResult = ScanValidationState.VALID,
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
