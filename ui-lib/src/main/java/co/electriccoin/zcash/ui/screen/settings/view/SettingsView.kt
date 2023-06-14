package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SwitchWithLabel
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens

@Preview("Settings")
@Composable
fun PreviewSettings() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Settings(
                isBackgroundSyncEnabled = true,
                isKeepScreenOnDuringSyncEnabled = true,
                isAnalyticsEnabled = true,
                isRescanEnabled = true,
                onBack = {},
                onRescanWallet = {},
                onBackgroundSyncSettingsChanged = {},
                onIsKeepScreenOnDuringSyncSettingsChanged = {},
                onAnalyticsSettingsChanged = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
fun Settings(
    isBackgroundSyncEnabled: Boolean,
    isKeepScreenOnDuringSyncEnabled: Boolean,
    isAnalyticsEnabled: Boolean,
    isRescanEnabled: Boolean,
    onBack: () -> Unit,
    onRescanWallet: () -> Unit,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onIsKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit
) {
    Scaffold(topBar = {
        SettingsTopAppBar(
            isRescanEnabled = isRescanEnabled,
            onBack = onBack,
            onRescanWallet = onRescanWallet
        )
    }) { paddingValues ->
        SettingsMainContent(
            isBackgroundSyncEnabled = isBackgroundSyncEnabled,
            isKeepScreenOnDuringSyncEnabled = isKeepScreenOnDuringSyncEnabled,
            isAnalyticsEnabled = isAnalyticsEnabled,
            onBackgroundSyncSettingsChanged = onBackgroundSyncSettingsChanged,
            onIsKeepScreenOnDuringSyncSettingsChanged = onIsKeepScreenOnDuringSyncSettingsChanged,
            onAnalyticsSettingsChanged = onAnalyticsSettingsChanged,
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacingDefault,
                    bottom = paddingValues.calculateTopPadding() + dimens.spacingDefault,
                    start = dimens.spacingDefault,
                    end = dimens.spacingDefault
                )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsTopAppBar(
    isRescanEnabled: Boolean,
    onBack: () -> Unit,
    onRescanWallet: () -> Unit
) {
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
        },
        actions = {
            if (isRescanEnabled) {
                TroubleshootingMenu(onRescanWallet)
            }
        }
    )
}

@Composable
private fun TroubleshootingMenu(
    onRescanWallet: () -> Unit
) {
    Column {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.settings_overflow_content_description)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_rescan)) },
                onClick = {
                    onRescanWallet()
                    expanded = false
                }
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun SettingsMainContent(
    isBackgroundSyncEnabled: Boolean,
    isKeepScreenOnDuringSyncEnabled: Boolean,
    isAnalyticsEnabled: Boolean,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onIsKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_background_sync),
            state = isBackgroundSyncEnabled,
            onStateChange = { onBackgroundSyncSettingsChanged(!isBackgroundSyncEnabled) }
        )

        Spacer(modifier = Modifier.height(dimens.spacingXlarge))

        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_keep_screen_on),
            state = isKeepScreenOnDuringSyncEnabled,
            onStateChange = { onIsKeepScreenOnDuringSyncSettingsChanged(!isKeepScreenOnDuringSyncEnabled) }
        )

        Spacer(modifier = Modifier.height(dimens.spacingXlarge))

        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_analytics),
            state = isAnalyticsEnabled,
            onStateChange = { onAnalyticsSettingsChanged(!isAnalyticsEnabled) }
        )
    }
}
