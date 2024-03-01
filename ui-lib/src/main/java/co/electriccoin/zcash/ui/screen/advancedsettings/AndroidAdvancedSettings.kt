@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.advancedsettings

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.screen.advancedsettings.view.AdvancedSettings

@Composable
internal fun WrapAdvancedSettings(
    goBack: () -> Unit,
    goExportPrivateData: () -> Unit,
    goSeedRecovery: () -> Unit,
    goChooseServer: () -> Unit,
) {
    WrapSettings(
        goBack = goBack,
        goExportPrivateData = goExportPrivateData,
        goChooseServer = goChooseServer,
        goSeedRecovery = goSeedRecovery,
    )
}

@Composable
@Suppress("LongParameterList")
private fun WrapSettings(
    goBack: () -> Unit,
    goExportPrivateData: () -> Unit,
    goChooseServer: () -> Unit,
    goSeedRecovery: () -> Unit,
) {
    BackHandler {
        goBack()
    }

    AdvancedSettings(
        onBack = goBack,
        onSeedRecovery = goSeedRecovery,
        onExportPrivateData = goExportPrivateData,
        onChooseServer = goChooseServer
    )
}
