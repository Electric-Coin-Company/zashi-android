package co.electriccoin.zcash.ui.screen.receive.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
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
                                items = listOf(),
                                isLoading = false,
                            ),
                        zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new()
                    )
                }
            }
        }
    }
}
