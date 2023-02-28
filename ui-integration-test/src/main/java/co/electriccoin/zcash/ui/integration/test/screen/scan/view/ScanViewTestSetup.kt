package co.electriccoin.zcash.ui.integration.test.screen.scan.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.integration.test.common.getPermissionNegativeButtonUiObject
import co.electriccoin.zcash.ui.integration.test.common.getPermissionPositiveButtonUiObject
import co.electriccoin.zcash.ui.screen.scan.model.ScanState
import co.electriccoin.zcash.ui.screen.scan.view.Scan
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class ScanViewTestSetup(
    private val composeTestRule: ComposeContentTestRule
) {
    private val onOpenSettingsCount = AtomicInteger(0)
    private val scanState = AtomicReference(ScanState.Permission)

    fun getOnOpenSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onOpenSettingsCount.get()
    }

    fun getScanState(): ScanState {
        composeTestRule.waitForIdle()
        return scanState.get()
    }

    fun grantPermission() {
        val permissionPositiveActionButton = getPermissionPositiveButtonUiObject()
        assertNotNull(permissionPositiveActionButton)
        assertTrue(permissionPositiveActionButton!!.exists())

        permissionPositiveActionButton.click()
    }

    fun denyPermission() {
        val permissionNegativeActionButton = getPermissionNegativeButtonUiObject()
        assertNotNull(permissionNegativeActionButton)
        assertTrue(permissionNegativeActionButton!!.exists())

        permissionNegativeActionButton.click()
    }

    @Composable
    fun DefaultContent() {
        Scan(
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onScanned = {},
            onOpenSettings = {
                onOpenSettingsCount.incrementAndGet()
            },
            onScanStateChanged = {
                scanState.set(it)
            }
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
