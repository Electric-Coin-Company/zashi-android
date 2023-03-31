package co.electriccoin.zcash.ui.screen.update.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.update.WrapUpdate
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo

class UpdateViewAndroidTestSetup(
    private val composeTestRule: AndroidComposeTestRule<*, *>,
    private val updateInfo: UpdateInfo
) {
    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        WrapUpdate(
            composeTestRule.activity,
            updateInfo
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
