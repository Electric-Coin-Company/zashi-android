package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.DangerousButton
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

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

@OptIn(ExperimentalMaterial3Api::class)
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
    SmallTopAppBar(
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
