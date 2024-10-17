package co.electriccoin.zcash.ui.screen.settings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import co.electriccoin.zcash.ui.screen.settings.model.SettingsState
import co.electriccoin.zcash.ui.screen.settings.model.SettingsTroubleshootingState
import kotlinx.collections.immutable.persistentListOf

@Suppress("LongMethod")
@Composable
fun Settings(
    state: SettingsState,
    topAppBarSubTitleState: TopAppBarSubTitleState
) {
    BlankBgScaffold(
        topBar = {
            SettingsTopAppBar(
                onBack = state.onBack,
                subTitleState = topAppBarSubTitleState,
                state = state
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            CircularScreenProgressIndicator()
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
                            start = 4.dp,
                            end = 4.dp
                        ),
            ) {
                ZashiSettingsListItem(
                    text = stringResource(id = R.string.settings_address_book),
                    icon = R.drawable.ic_settings_address_book,
                    onClick = state.onAddressBookClick
                )
                ZashiHorizontalDivider()
                ZashiSettingsListItem(state = state.integrations)
                ZashiHorizontalDivider()
                ZashiSettingsListItem(
                    text = stringResource(id = R.string.settings_advanced_settings),
                    icon = R.drawable.ic_advanced_settings,
                    onClick = state.onAdvancedSettingsClick
                )
                ZashiHorizontalDivider()
                ZashiSettingsListItem(
                    text = stringResource(id = R.string.settings_about_us),
                    icon = R.drawable.ic_settings_info,
                    onClick = state.onAboutUsClick
                )
                ZashiHorizontalDivider()
                ZashiSettingsListItem(
                    text = stringResource(id = R.string.settings_feedback),
                    icon = R.drawable.ic_settings_feedback,
                    onClick = state.onSendUsFeedbackClick
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingMin))
                Image(
                    modifier = Modifier.align(CenterHorizontally),
                    painter =
                        painterResource(id = R.drawable.ic_settings_zashi),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = state.version.getValue(),
                    color = ZashiColors.Text.textTertiary
                )
            }
        }
    }
}

@Composable
private fun SettingsTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState,
    state: SettingsState
) {
    ZashiSmallTopAppBar(
        title = stringResource(id = R.string.settings_title),
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(SettingsTag.SETTINGS_TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
        regularActions = {
            if (state.settingsTroubleshootingState != null) {
                TroubleshootingMenu(state = state.settingsTroubleshootingState)
            }
        },
    )
}

@Composable
private fun TroubleshootingMenu(state: SettingsTroubleshootingState) {
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
                    state.backgroundSync.onClick()
                    expanded = false
                },
                leadingIcon = { AddIcon(state.backgroundSync.isEnabled) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_troubleshooting_enable_keep_screen_on)) },
                onClick = {
                    state.keepScreenOnDuringSync.onClick()
                    expanded = false
                },
                leadingIcon = { AddIcon(state.keepScreenOnDuringSync.isEnabled) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.settings_troubleshooting_enable_analytics)) },
                onClick = {
                    state.analytics.onClick()
                    expanded = false
                },
                leadingIcon = { AddIcon(state.analytics.isEnabled) }
            )
            // isRescanEnabled means if this feature should be visible, not whether it is enabled as in the case of
            // the previous booleans
            if (state.rescan.isEnabled) {
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.settings_troubleshooting_rescan)) },
                    onClick = {
                        state.rescan.onClick()
                        expanded = false
                    },
                    leadingIcon = { AddIcon(true) }
                )
            }
        }
    }
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

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun PreviewSettings() {
    ZcashTheme {
        Settings(
            state =
                SettingsState(
                    isLoading = false,
                    version = stringRes("Version 1.2"),
                    settingsTroubleshootingState = null,
                    onBack = {},
                    onAdvancedSettingsClick = {},
                    onAboutUsClick = {},
                    onSendUsFeedbackClick = {},
                    onAddressBookClick = {},
                    integrations =
                        ZashiSettingsListItemState(
                            icon = R.drawable.ic_settings_integrations,
                            text = stringRes("Integrations"),
                            titleIcons = persistentListOf(R.drawable.ic_integrations_coinbase)
                        ) {}
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun PreviewSettingsLoading() {
    ZcashTheme {
        Settings(
            state =
                SettingsState(
                    isLoading = true,
                    version = stringRes("Version 1.2"),
                    settingsTroubleshootingState = null,
                    onBack = {},
                    onAdvancedSettingsClick = {},
                    onAboutUsClick = {},
                    onSendUsFeedbackClick = {},
                    onAddressBookClick = {},
                    integrations =
                        ZashiSettingsListItemState(
                            icon = R.drawable.ic_settings_integrations,
                            text = stringRes("Integrations"),
                            titleIcons = persistentListOf(R.drawable.ic_integrations_coinbase)
                        ) {}
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}
