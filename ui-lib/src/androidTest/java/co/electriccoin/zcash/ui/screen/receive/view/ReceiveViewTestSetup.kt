package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalScreenBrightness
import co.electriccoin.zcash.ui.common.compose.LocalScreenTimeout
import co.electriccoin.zcash.ui.common.compose.ScreenBrightness
import co.electriccoin.zcash.ui.common.compose.ScreenTimeout
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.test.getStringResource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ReceiveViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    walletAddresses: WalletAddresses,
    versionInfo: VersionInfo = VersionInfoFixture.new()
) {
    private val onSettingsCount = AtomicInteger(0)
    private val onAddressDetailsCount = AtomicInteger(0)
    private val screenBrightness = ScreenBrightness()
    private val screenTimeout = ScreenTimeout()
    private val onAdjustBrightness = AtomicBoolean(false)

    fun getScreenBrightnessCount() = screenBrightness.referenceCount.value

    fun getScreenTimeoutCount() = screenTimeout.referenceCount.value

    fun getOnAdjustBrightness(): Boolean {
        composeTestRule.waitForIdle()
        return onAdjustBrightness.get()
    }

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    fun getOnAddressDetailsCount(): Int {
        composeTestRule.waitForIdle()
        return onAddressDetailsCount.get()
    }

    init {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalScreenBrightness provides screenBrightness,
                LocalScreenTimeout provides screenTimeout
            ) {
                ZcashTheme {
                    ZcashTheme {
                        Receive(
                            walletAddress = walletAddresses,
                            snackbarHostState = SnackbarHostState(),
                            onSettings = {
                                onSettingsCount.getAndIncrement()
                            },
                            onAdjustBrightness = {
                                onAdjustBrightness.getAndSet(it)
                            },
                            onAddrCopyToClipboard = {},
                            onQrImageShare = {},
                            versionInfo = versionInfo
                        )
                    }
                }
            }
        }
    }
}

fun ComposeContentTestRule.clickAdjustBrightness() {
    onNodeWithContentDescription(getStringResource(R.string.receive_brightness_content_description)).also {
        it.performClick()
    }
}
