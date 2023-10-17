package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo
import java.util.concurrent.atomic.AtomicInteger

class AboutViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    versionInfo: VersionInfo,
    configInfo: ConfigInfo
) {

    private val onBackCount = AtomicInteger(0)

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
    }

    init {
        composeTestRule.setContent {
            ZcashTheme {
                About(
                    onBack = { onBackCount.incrementAndGet() },
                    versionInfo = versionInfo,
                    configInfo = configInfo
                )
            }
        }
    }
}
