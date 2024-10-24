package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.fixture.ConfigInfoFixture
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

@Composable
@Suppress("LongParameterList")
fun About(
    onBack: () -> Unit,
    configInfo: ConfigInfo,
    onPrivacyPolicy: () -> Unit,
    onWhatsNew: () -> Unit,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    versionInfo: VersionInfo,
) {
    BlankBgScaffold(
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
            onWhatsNew = onWhatsNew,
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .scaffoldPadding(paddingValues)
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
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.about_title),
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
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
    onWhatsNew: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Image(
            modifier =
                Modifier
                    .height(ZcashTheme.dimens.inScreenZcashTextLogoHeight)
                    .align(Alignment.CenterHorizontally),
            painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_text_logo_small),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = stringResource(R.string.zcash_logo_content_description)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text =
                stringResource(
                    R.string.about_version_format,
                    versionInfo.versionName
                ),
            textAlign = TextAlign.Center,
            style = ZcashTheme.typography.primary.titleSmall
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text = stringResource(id = R.string.about_description),
            color = ZcashTheme.colors.textDescriptionDark,
            style = ZcashTheme.extendedTypography.aboutText
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onWhatsNew,
            text = stringResource(R.string.about_button_whats_new),
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPrivacyPolicy,
            text = stringResource(R.string.about_button_privacy_policy),
        )
    }
}

@Composable
private fun AboutPreview() {
    About(
        onBack = {},
        configInfo = ConfigInfoFixture.new(),
        onPrivacyPolicy = {},
        onWhatsNew = {},
        snackbarHostState = SnackbarHostState(),
        topAppBarSubTitleState = TopAppBarSubTitleState.None,
        versionInfo = VersionInfoFixture.new(),
    )
}

@Preview("About")
@Composable
private fun AboutPreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        AboutPreview()
    }

@Preview("About")
@Composable
private fun AboutPreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        AboutPreview()
    }
