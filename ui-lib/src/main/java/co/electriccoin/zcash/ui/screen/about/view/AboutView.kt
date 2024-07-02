package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ConfigInfoFixture
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

@Preview("About")
@Composable
private fun AboutPreview() {
    ZcashTheme(forceDarkMode = false) {
        About(
            onBack = {},
            configInfo = ConfigInfoFixture.new(),
            onPrivacyPolicy = {},
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            versionInfo = VersionInfoFixture.new(),
        )
    }
}

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
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
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
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.about_title).uppercase(),
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
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
    onPrivacyPolicy: () -> Unit,
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val logoContentDescription = stringResource(R.string.zcash_logo_content_description)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = logoContentDescription
                }
        ) {
            Image(
                painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_logo_without_text),
                colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
                contentDescription = null,
                modifier =
                    Modifier
                        .height(ZcashTheme.dimens.inScreenZcashLogoHeight)
                        .width(ZcashTheme.dimens.inScreenZcashLogoWidth)
            )
            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))
            Image(
                painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.zashi_text_logo),
                colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
                contentDescription = null,
                modifier = Modifier.height(ZcashTheme.dimens.inScreenZcashTextLogoHeight)
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text =
                stringResource(
                    R.string.about_version_format,
                    versionInfo.versionName
                ),
            style = ZcashTheme.typography.primary.titleSmall
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text = stringResource(id = R.string.about_description),
            color = ZcashTheme.colors.textDescriptionDark,
            style = ZcashTheme.extendedTypography.aboutText
        )

        PrivacyPolicyLink(onPrivacyPolicy)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
fun PrivacyPolicyLink(onPrivacyPolicy: () -> Unit) {
    Column {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        val textPart1 = stringResource(R.string.about_pp_part_1)
        val textPart2 = stringResource(R.string.about_pp_part_2)
        val textPart3 = stringResource(R.string.about_pp_part_3)

        ClickableText(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(color = ZcashTheme.colors.textDescriptionDark)) {
                        append(textPart1)
                    }
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = ZcashTheme.colors.textDescriptionDark,
                        )
                    ) {
                        append(textPart2)
                    }
                    withStyle(SpanStyle(color = ZcashTheme.colors.textDescriptionDark)) {
                        append(textPart3)
                    }
                },
            style = ZcashTheme.extendedTypography.aboutText,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(AboutTag.PP_TEXT_TAG),
            onClick = { letterOffset ->
                // Call the callback only if user clicked the underlined part
                if (letterOffset >= textPart1.length && letterOffset <= (textPart1.length + textPart2.length)) {
                    onPrivacyPolicy()
                }
            }
        )
    }
}
