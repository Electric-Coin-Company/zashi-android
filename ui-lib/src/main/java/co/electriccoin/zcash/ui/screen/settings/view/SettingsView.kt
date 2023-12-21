package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import co.electriccoin.zcash.ui.screen.settings.model.TroubleshootingParameters

@Preview("Settings")
@Composable
private fun PreviewSettings() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Settings(
                TroubleshootingParameters(
                    isEnabled = false,
                    isBackgroundSyncEnabled = false,
                    isKeepScreenOnDuringSyncEnabled = false,
                    isAnalyticsEnabled = false,
                    isRescanEnabled = false
                ),
                onBack = {},
                onSeedRecovery = {},
                onDocumentation = {},
                onPrivacyPolicy = {},
                onFeedback = {},
                onExportPrivateData = {},
                onAbout = {},
                onRescanWallet = {},
                onBackgroundSyncSettingsChanged = {},
                onKeepScreenOnDuringSyncSettingsChanged = {},
                onAnalyticsSettingsChanged = {}
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun Settings(
    troubleshootingParameters: TroubleshootingParameters,
    onBack: () -> Unit,
    onSeedRecovery: () -> Unit,
    onDocumentation: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onFeedback: () -> Unit,
    onExportPrivateData: () -> Unit,
    onAbout: () -> Unit,
    onRescanWallet: () -> Unit,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit
) {
    Scaffold(topBar = {
        SettingsTopAppBar(
            troubleshootingParameters = troubleshootingParameters,
            onBackgroundSyncSettingsChanged = onBackgroundSyncSettingsChanged,
            onKeepScreenOnDuringSyncSettingsChanged = onKeepScreenOnDuringSyncSettingsChanged,
            onAnalyticsSettingsChanged = onAnalyticsSettingsChanged,
            onRescanWallet = onRescanWallet,
            onBack = onBack,
        )
    }) { paddingValues ->
        SettingsMainContent(
            modifier =
                Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding() + dimens.spacingHuge,
                        bottom = paddingValues.calculateBottomPadding(),
                        start = dimens.screenHorizontalSpacingBig,
                        end = dimens.screenHorizontalSpacingBig
                    ),
            onSeedRecovery = onSeedRecovery,
            onDocumentation = onDocumentation,
            onPrivacyPolicy = onPrivacyPolicy,
            onFeedback = onFeedback,
            onExportPrivateData = onExportPrivateData,
            onAbout = onAbout,
        )
    }
}

@Composable
@Suppress("LongParameterList")
private fun SettingsTopAppBar(
    troubleshootingParameters: TroubleshootingParameters,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit,
    onRescanWallet: () -> Unit,
    onBack: () -> Unit,
) {
    SmallTopAppBar(
        backText = stringResource(id = R.string.settings_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.settings_back_content_description),
        onBack = onBack,
        showTitleLogo = true,
        regularActions = {
            if (troubleshootingParameters.isEnabled) {
                TroubleshootingMenu(
                    troubleshootingParameters,
                    onBackgroundSyncSettingsChanged,
                    onKeepScreenOnDuringSyncSettingsChanged,
                    onAnalyticsSettingsChanged,
                    onRescanWallet
                )
            }
        },
        modifier = Modifier.testTag(SettingsTag.SETTINGS_TOP_APP_BAR)
    )
}

/**
 * Add icon to Troubleshooting menu. No content description, as this is debug only menu.
 */
@Composable
private fun AddIcon(enabled: Boolean) {
    if (enabled) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null
        )
    } else {
        Icon(
            imageVector = Icons.Outlined.Cancel,
            contentDescription = null
        )
    }
}

@Composable
private fun TroubleshootingMenu(
    troubleshootParams: TroubleshootingParameters,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit,
    onRescanWallet: () -> Unit
) {
    Column(
        modifier = Modifier.testTag(SettingsTag.TROUBLESHOOTING_MENU)
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.settings_troubleshooting_menu_content_description)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_troubleshooting_enable_background_sync)) },
                onClick = {
                    onBackgroundSyncSettingsChanged(!troubleshootParams.isBackgroundSyncEnabled)
                    expanded = false
                },
                leadingIcon = { AddIcon(troubleshootParams.isBackgroundSyncEnabled) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_troubleshooting_enable_keep_screen_on)) },
                onClick = {
                    onKeepScreenOnDuringSyncSettingsChanged(!troubleshootParams.isKeepScreenOnDuringSyncEnabled)
                    expanded = false
                },
                leadingIcon = { AddIcon(troubleshootParams.isKeepScreenOnDuringSyncEnabled) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_troubleshooting_enable_analytics)) },
                onClick = {
                    onAnalyticsSettingsChanged(!troubleshootParams.isAnalyticsEnabled)
                    expanded = false
                },
                leadingIcon = { AddIcon(troubleshootParams.isAnalyticsEnabled) }
            )
            // isRescanEnabled means if this feature should be visible, not whether it is enabled as in the case of
            // the previous booleans
            if (troubleshootParams.isRescanEnabled) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.settings_troubleshooting_rescan)) },
                    onClick = {
                        onRescanWallet()
                        expanded = false
                    },
                    leadingIcon = { AddIcon(true) }
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
private fun SettingsMainContent(
    onSeedRecovery: () -> Unit,
    onDocumentation: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onFeedback: () -> Unit,
    onExportPrivateData: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            onClick = onSeedRecovery,
            text = stringResource(R.string.settings_backup_wallet)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onFeedback,
            text = stringResource(R.string.settings_send_us_feedback)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onPrivacyPolicy,
            text = stringResource(R.string.settings_privacy_policy)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onDocumentation,
            text = stringResource(R.string.settings_documentation)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onExportPrivateData,
            text = stringResource(R.string.settings_export_private_data)
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onAbout,
            text = stringResource(R.string.settings_about)
        )

        Spacer(modifier = Modifier.height(dimens.spacingHuge))
    }
}
