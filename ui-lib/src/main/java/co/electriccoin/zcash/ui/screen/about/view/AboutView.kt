package co.electriccoin.zcash.ui.screen.about.view

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiVersion
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ConfigInfoFixture
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

@Composable
@Suppress("LongParameterList")
fun About(
    onBack: () -> Unit,
    configInfo: ConfigInfo,
    onPrivacyPolicy: () -> Unit,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    versionInfo: VersionInfo,
) {
    Scaffold(
        topBar = {
            AboutTopAppBar(
                onBack = onBack,
                versionInfo = versionInfo,
                configInfo = configInfo,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        AboutMainContent(
            versionInfo = versionInfo,
            onPrivacyPolicy = onPrivacyPolicy,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
                        bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
                        start = 4.dp,
                        end = 4.dp
                    )
        )
    }
}

@Composable
private fun AboutTopAppBar(
    onBack: () -> Unit,
    versionInfo: VersionInfo,
    configInfo: ConfigInfo,
    subTitleState: TopAppBarSubTitleState
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        title = stringResource(id = R.string.about_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
        regularActions = {
            if (versionInfo.isDebuggable && !versionInfo.isRunningUnderTestService) {
                DebugMenu(versionInfo, configInfo)
            }
        },
    )
}

@Composable
private fun DebugMenu(
    versionInfo: VersionInfo,
    configInfo: ConfigInfo
) {
    Column(
        modifier = Modifier.testTag(AboutTag.DEBUG_MENU_TAG)
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            stringResource(
                                id = R.string.about_debug_menu_app_name,
                                stringResource(id = R.string.app_name)
                            )
                        )
                        Text(stringResource(R.string.about_debug_menu_build, versionInfo.gitSha))
                        Text(configInfo.toSupportString())
                    }
                },
                onClick = {
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun AboutMainContent(
    onPrivacyPolicy: () -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(horizontal = ZashiDimensions.Spacing.spacingXl),
            text = stringResource(id = R.string.about_subtitle),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.header6,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = ZashiDimensions.Spacing.spacingXl),
            text = stringResource(id = R.string.about_description),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textSm
        )

        Spacer(Modifier.height(32.dp))

        ZashiSettingsListItem(
            ZashiSettingsListItemState(
                icon = R.drawable.ic_settings_info,
                text = stringRes(R.string.about_button_privacy_policy),
                onClick = onPrivacyPolicy
            )
        )

        Spacer(Modifier.weight(1f))

        ZashiVersion(
            modifier = Modifier.fillMaxWidth(),
            version = stringRes(R.string.settings_version, versionInfo.versionName)
        )
    }
}

@PreviewScreens
@Composable
private fun AboutPreview() =
    ZcashTheme {
        About(
            onBack = {},
            configInfo = ConfigInfoFixture.new(),
            onPrivacyPolicy = {},
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            versionInfo = VersionInfoFixture.new(),
        )
    }
