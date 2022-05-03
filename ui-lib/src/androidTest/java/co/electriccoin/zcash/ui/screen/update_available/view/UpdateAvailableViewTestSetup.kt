package co.electriccoin.zcash.ui.screen.update_available.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update_available.model.UpdateState
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class UpdateAvailableViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    private val updateInfo: UpdateInfo
) {

    private val onDownloadCount = AtomicInteger(0)
    private val onLaterCount = AtomicInteger(0)
    private val onReferenceCount = AtomicInteger(0)
    private val updateState = AtomicReference(UpdateState.Prepared)

    fun getOnDownloadCount(): Int {
        composeTestRule.waitForIdle()
        return onDownloadCount.get()
    }

    fun getOnLaterCount(): Int {
        composeTestRule.waitForIdle()
        return onLaterCount.get()
    }

    fun getOnReferenceCount(): Int {
        composeTestRule.waitForIdle()
        return onReferenceCount.get()
    }

    fun getUpdateState(): UpdateState {
        composeTestRule.waitForIdle()
        return updateState.get()
    }
    fun getUpdateInfo(): UpdateInfo {
        composeTestRule.waitForIdle()
        return updateInfo
    }

    @Composable
    fun getDefaultContent() {
        UpdateAvailable(
            updateInfo = updateInfo,
            onDownload = { newState ->
                onDownloadCount.incrementAndGet()
                updateState.set(newState)
            },
            onLater = {
                onLaterCount.incrementAndGet()
            },
            onReference = {
                onReferenceCount.incrementAndGet()
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
