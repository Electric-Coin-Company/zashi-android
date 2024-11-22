package co.electriccoin.zcash.ui.screen.settings.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiVersion
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.settings.SettingsTag
import co.electriccoin.zcash.ui.screen.settings.model.SettingsState
import co.electriccoin.zcash.ui.screen.settings.model.SettingsTroubleshootingState
import kotlinx.collections.immutable.persistentListOf

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
            state.items.forEachIndexed { index, item ->
                ZashiListItem(state = item)
                if (index != state.items.lastIndex) {
                    ZashiHorizontalDivider()
                }
            }
            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))
            Spacer(modifier = Modifier.weight(1f))
            ZashiVersion(modifier = Modifier.align(CenterHorizontally), version = state.version)
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
            if (state.debugMenu != null) {
                TroubleshootingMenu(state = state.debugMenu)
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

@PreviewScreens
@Composable
private fun PreviewSettings() {
    ZcashTheme {
        Settings(
            state =
                SettingsState(
                    version = stringRes("Version 1.2"),
                    debugMenu = null,
                    onBack = {},
                    items =
                        persistentListOf(
                            ZashiListItemState(
                                title = stringRes(R.string.settings_address_book),
                                icon = R.drawable.ic_settings_address_book,
                                onClick = { },
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.settings_integrations),
                                icon = R.drawable.ic_settings_integrations,
                                onClick = { },
                                titleIcons = persistentListOf(R.drawable.ic_integrations_coinbase)
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.settings_advanced_settings),
                                icon = R.drawable.ic_advanced_settings,
                                onClick = { },
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.settings_about_us),
                                icon = R.drawable.ic_settings_info,
                                onClick = { },
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.settings_feedback),
                                icon = R.drawable.ic_settings_feedback,
                                onClick = { },
                            ),
                        ),
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}
