package co.electriccoin.zcash.ui.screen.scan.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

// TODO [#313]: https://github.com/zcash/secant-android-wallet/issues/313
class ScanViewTestSetup(
    private val composeTestRule: ComposeContentTestRule
) {
    private val onScanCount = AtomicInteger(0)
    private val onOpenSettingsCount = AtomicInteger(0)
    private val onBackCount = AtomicInteger(0)
    private val scanState = AtomicReference(ScanState.Permission)

    fun getOnScanCount(): Int {
        composeTestRule.waitForIdle()
        return onScanCount.get()
    }

    fun getOnOpenSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onOpenSettingsCount.get()
    }

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getScanState(): ScanState {
        composeTestRule.waitForIdle()
        return scanState.get()
    }

    @Composable
    fun getDefaultContent() {
        Scan(
            snackbarHostState = SnackbarHostState(),
            onBack = {
                onBackCount.incrementAndGet()
            },
            onScanDone = {
                onScanCount.incrementAndGet()
            },
            onOpenSettings = {
                onOpenSettingsCount.incrementAndGet()
            }
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
