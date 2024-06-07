package co.electriccoin.zcash.ui.screen.scan.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class ScanViewBasicTestSetup(
    private val composeTestRule: ComposeContentTestRule
) {
    private val onBackCount = AtomicInteger(0)
    private val scanState = AtomicReference(ScanState.Permission)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    fun getScanState(): ScanState {
        composeTestRule.waitForIdle()
        return scanState.get()
    }

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        Scan(
            addressValidationResult = AddressType.Shielded,
            onBack = {
                onBackCount.incrementAndGet()
            },
            onScanned = {},
            onOpenSettings = {},
            onScanStateChanged = {
                scanState.set(it)
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
