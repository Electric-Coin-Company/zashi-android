package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
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
    onRescanWallet: () -> Unit
) {
    Scaffold(topBar = {
        SettingsTopAppBar(onBack = onBack)
    }) { paddingValues ->
        SettingsMainContent(
            paddingValues,
            onBackupWallet = onBackupWallet,
            onWipeWallet = onWipeWallet,
            onRescanWallet = onRescanWallet
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
    paddingValues: PaddingValues,
    onBackupWallet: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onWipeWallet: () -> Unit,
    onRescanWallet: () -> Unit
) {
    Column(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        PrimaryButton(onClick = onBackupWallet, text = stringResource(id = R.string.settings_backup))
        // We have decided to not include this in settings; see overflow debug menu instead
        // DangerousButton(onClick = onWipeWallet, text = stringResource(id = R.string.settings_wipe))
        TertiaryButton(onClick = onRescanWallet, text = stringResource(id = R.string.settings_rescan))
    }
}
