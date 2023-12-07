package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.LocalScreenBrightness
import co.electriccoin.zcash.ui.common.LocalScreenTimeout
import co.electriccoin.zcash.ui.common.ScreenBrightness
import co.electriccoin.zcash.ui.common.ScreenTimeout
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.test.getStringResource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ReceiveViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    walletAddress: WalletAddress
) {
    private val onBackCount = AtomicInteger(0)
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

    fun getOnBackCount(): Int {
        composeTestRule.waitForIdle()
        return onBackCount.get()
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
                            walletAddress,
                            onBack = {
                                onBackCount.getAndIncrement()
                            },
                            onAddressDetails = {
                                onAddressDetailsCount.getAndIncrement()
                            },
                            onAdjustBrightness = {
                                onAdjustBrightness.getAndSet(it)
                            },
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
