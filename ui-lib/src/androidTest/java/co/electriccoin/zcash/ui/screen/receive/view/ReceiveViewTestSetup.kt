package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.android.sdk.model.WalletAddresses
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import java.util.concurrent.atomic.AtomicInteger

class ReceiveViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
    walletAddresses: WalletAddresses,
    versionInfo: VersionInfo = VersionInfoFixture.new()
) {
    private val onSettingsCount = AtomicInteger(0)
    private val onAddressDetailsCount = AtomicInteger(0)

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
            ZcashTheme {
                ZcashTheme {
                    Receive(
                        walletAddresses = walletAddresses,
                        snackbarHostState = SnackbarHostState(),
                        onSettings = {
                            onSettingsCount.getAndIncrement()
                        },
                        onAddrCopyToClipboard = {},
                        onQrCode = {},
                        topAppBarSubTitleState = TopAppBarSubTitleState.None,
                        versionInfo = versionInfo,
                    )
                }
            }
        }
    }
}
