package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressState
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveState
import java.util.concurrent.atomic.AtomicInteger

class ReceiveViewTestSetup(
    private val composeTestRule: ComposeContentTestRule,
) {
    private val onSettingsCount = AtomicInteger(0)

    fun getOnSettingsCount(): Int {
        composeTestRule.waitForIdle()
        return onSettingsCount.get()
    }

    init {
        composeTestRule.setContent {
            ZcashTheme {
                ZcashTheme {
                    ReceiveView(
                        state =
                            ReceiveState(
                                items =
                                    listOf(
                                        ReceiveAddressState(
                                            icon = R.drawable.ic_zec_round_full,
                                            title = stringRes("Zashi"),
                                            subtitle =
                                                stringRes(
                                                    "${WalletAddressFixture.UNIFIED_ADDRESS_STRING.take(20)}...",
                                                ),
                                            isShielded = true,
                                            onCopyClicked = {},
                                            onQrClicked = { },
                                            onRequestClicked = {},
                                            isExpanded = true,
                                            onClick = {}
                                        )
                                    ),
                                isLoading = false,
                            ),
                        zashiMainTopAppBarState =
                            ZashiMainTopAppBarStateFixture.new(
                                settingsButton =
                                    IconButtonState(
                                        icon = co.electriccoin.zcash.ui.design.R.drawable.ic_app_bar_settings,
                                        contentDescription =
                                            stringRes(R.string.settings_menu_content_description),
                                    ) {
                                        onSettingsCount.incrementAndGet()
                                    }
                            )
                    )
                }
            }
        }
    }
}
