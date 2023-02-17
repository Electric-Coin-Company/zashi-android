package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
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
                isBackgroundSyncEnabled = true,
                isKeepScreenOnDuringSyncEnabled = true,
                isAnalyticsEnabled = true,
                onBack = {},
                onBackupWallet = {},
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
    onBack: () -> Unit,
    onBackupWallet: () -> Unit,
    onRescanWallet: () -> Unit,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onIsKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit
) {
    Scaffold(topBar = {
        SettingsTopAppBar(onBack = onBack)
    }) { paddingValues ->
        SettingsMainContent(
            paddingValues,
            isBackgroundSyncEnabled = isBackgroundSyncEnabled,
            isKeepScreenOnDuringSyncEnabled = isKeepScreenOnDuringSyncEnabled,
            isAnalyticsEnabled = isAnalyticsEnabled,
            onBackupWallet = onBackupWallet,
            onRescanWallet = onRescanWallet,
            onBackgroundSyncSettingsChanged = onBackgroundSyncSettingsChanged,
            onIsKeepScreenOnDuringSyncSettingsChanged = onIsKeepScreenOnDuringSyncSettingsChanged,
            onAnalyticsSettingsChanged = onAnalyticsSettingsChanged
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
@Suppress("LongParameterList")
private fun SettingsMainContent(
    paddingValues: PaddingValues,
    isBackgroundSyncEnabled: Boolean,
    isKeepScreenOnDuringSyncEnabled: Boolean,
    isAnalyticsEnabled: Boolean,
    onBackupWallet: () -> Unit,
    onRescanWallet: () -> Unit,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onIsKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit
) {
    Column(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        PrimaryButton(onClick = onBackupWallet, text = stringResource(id = R.string.settings_backup))
        TertiaryButton(onClick = onRescanWallet, text = stringResource(id = R.string.settings_rescan))
        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_background_sync),
            state = isBackgroundSyncEnabled,
            onStateChange = { onBackgroundSyncSettingsChanged(!isBackgroundSyncEnabled) }
        )
        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_keep_screen_on),
            state = isKeepScreenOnDuringSyncEnabled,
            onStateChange = { onIsKeepScreenOnDuringSyncSettingsChanged(!isKeepScreenOnDuringSyncEnabled) }
        )
        SwitchWithLabel(
            label = stringResource(id = R.string.settings_enable_analytics),
            state = isAnalyticsEnabled,
            onStateChange = { onAnalyticsSettingsChanged(!isAnalyticsEnabled) }
        )
    }
}

@Composable
private fun SwitchWithLabel(label: String, state: Boolean, onStateChange: (Boolean) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // disable ripple
                role = Role.Switch,
                onClick = { onStateChange(!state) }
            )
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Body(text = label)
        Spacer(modifier = Modifier.fillMaxWidth(MINIMAL_WEIGHT))
        Switch(
            checked = state,
            onCheckedChange = {
                onStateChange(it)
            }
        )
    }
}
