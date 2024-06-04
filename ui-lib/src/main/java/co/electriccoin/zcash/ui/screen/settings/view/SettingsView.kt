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
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
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
        Settings(
            onAbout = {},
            onAdvancedSettings = {},
            onBack = {},
            onFeedback = {},
            onRescanWallet = {},
            onBackgroundSyncSettingsChanged = {},
            onKeepScreenOnDuringSyncSettingsChanged = {},
            onAnalyticsSettingsChanged = {},
            troubleshootingParameters =
                TroubleshootingParameters(
                    isEnabled = false,
                    isBackgroundSyncEnabled = false,
                    isKeepScreenOnDuringSyncEnabled = false,
                    isAnalyticsEnabled = false,
                    isRescanEnabled = false
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun Settings(
    onAbout: () -> Unit,
    onAdvancedSettings: () -> Unit,
    onBack: () -> Unit,
    onFeedback: () -> Unit,
    onRescanWallet: () -> Unit,
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit,
    troubleshootingParameters: TroubleshootingParameters,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    BlankBgScaffold(topBar = {
        SettingsTopAppBar(
            troubleshootingParameters = troubleshootingParameters,
            onBackgroundSyncSettingsChanged = onBackgroundSyncSettingsChanged,
            onKeepScreenOnDuringSyncSettingsChanged = onKeepScreenOnDuringSyncSettingsChanged,
            onAnalyticsSettingsChanged = onAnalyticsSettingsChanged,
            onRescanWallet = onRescanWallet,
            onBack = onBack,
            subTitleState = topAppBarSubTitleState,
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
            onAbout = onAbout,
            onAdvancedSettings = onAdvancedSettings,
            onFeedback = onFeedback,
        )
    }
}

@Composable
@Suppress("LongParameterList")
private fun SettingsTopAppBar(
    onBackgroundSyncSettingsChanged: (Boolean) -> Unit,
    onKeepScreenOnDuringSyncSettingsChanged: (Boolean) -> Unit,
    onAnalyticsSettingsChanged: (Boolean) -> Unit,
    onRescanWallet: () -> Unit,
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
    troubleshootingParameters: TroubleshootingParameters,
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(SettingsTag.SETTINGS_TOP_APP_BAR),
        showTitleLogo = true,
        backText = stringResource(id = R.string.settings_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.settings_back_content_description),
        onBack = onBack,
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
private fun SettingsMainContent(
    onAbout: () -> Unit,
    onAdvancedSettings: () -> Unit,
    onFeedback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            onClick = onFeedback,
            text = stringResource(R.string.settings_send_us_feedback),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = onAdvancedSettings,
            text = stringResource(R.string.settings_advanced_settings),
            modifier = Modifier.fillMaxWidth()
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
            text = stringResource(R.string.settings_about),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingHuge))
    }
}
