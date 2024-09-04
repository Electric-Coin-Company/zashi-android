package co.electriccoin.zcash.ui.screen.update.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerMock
import co.electriccoin.zcash.ui.screen.update.WrapUpdate
import co.electriccoin.zcash.ui.screen.update.model.UpdateInfo
import co.electriccoin.zcash.ui.screen.update.viewmodel.UpdateViewModel

class UpdateViewAndroidTestSetup(
    updateInfo: UpdateInfo,
    private val composeTestRule: AndroidComposeTestRule<*, *>
) {
    private val viewModel =
        UpdateViewModel(
            application = composeTestRule.activity.application,
            updateInfo = updateInfo,
            appUpdateChecker = AppUpdateCheckerMock.new()
        )

    @Composable
    @Suppress("TestFunctionName")
    fun DefaultContent() {
        CompositionLocalProvider(LocalActivity provides composeTestRule.activity) {
            val updateInfo = viewModel.updateInfo.collectAsStateWithLifecycle().value
            // val appUpdateInfo = updateInfo.appUpdateInfo
            WrapUpdate(
                updateInfo = updateInfo,
                checkForUpdate = viewModel::checkForAppUpdate,
                remindLater = viewModel::remindLater,
                goForUpdate = {}
            )
        }
    }

    fun setDefaultContent() {
        composeTestRule.setContent {
            ZcashTheme {
                DefaultContent()
            }
        }
    }
}
