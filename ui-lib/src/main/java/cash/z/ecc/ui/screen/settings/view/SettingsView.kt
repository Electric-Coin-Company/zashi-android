package cash.z.ecc.ui.screen.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.ui.R
import cash.z.ecc.ui.screen.common.DangerousButton
import cash.z.ecc.ui.screen.common.GradientSurface
import cash.z.ecc.ui.screen.common.PrimaryButton
import cash.z.ecc.ui.screen.common.TertiaryButton
import cash.z.ecc.ui.theme.ZcashTheme

@Preview("Settings")
@Composable
fun PreviewSettings() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Settings(
                onBack = {},
                onBackupWallet = {},
                onWipeWallet = {},
                onRescanWallet = {}
            )
        }
    }
}

@Composable
fun Settings(
    onBack: () -> Unit,
    onBackupWallet: () -> Unit,
    onWipeWallet: () -> Unit,
    onRescanWallet: () -> Unit,
) {
    Scaffold(topBar = {
        SettingsTopAppBar(onBack = onBack)
    }) {
        SettingsMainContent(
            onBackupWallet = onBackupWallet,
            onWipeWallet = onWipeWallet,
            onRescanWallet = onRescanWallet
        )
    }
}

@Composable
private fun SettingsTopAppBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.settings_header)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back_content_description)
                )
            }
        }
    )
}

@Composable
private fun SettingsMainContent(
    onBackupWallet: () -> Unit,
    onWipeWallet: () -> Unit,
    onRescanWallet: () -> Unit
) {
    Column {
        PrimaryButton(onClick = onBackupWallet, text = stringResource(id = R.string.settings_backup))
        DangerousButton(onClick = onWipeWallet, text = stringResource(id = R.string.settings_wipe))
        TertiaryButton(onClick = onRescanWallet, text = stringResource(id = R.string.settings_rescan))
    }
}
