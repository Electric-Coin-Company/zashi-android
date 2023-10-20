package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ConfigInfoFixture
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.AboutTag
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

@Preview("About")
@Composable
private fun AboutPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            About(
                onBack = {},
                versionInfo = VersionInfoFixture.new(),
                configInfo = ConfigInfoFixture.new()
            )
        }
    }
}

@Composable
fun About(
    onBack: () -> Unit,
    versionInfo: VersionInfo,
    configInfo: ConfigInfo
) {
    Scaffold(topBar = {
        AboutTopAppBar(
            onBack = onBack,
            versionInfo = versionInfo,
            configInfo = configInfo
        )
    }) { paddingValues ->
        AboutMainContent(
            versionInfo = versionInfo,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                    start = ZcashTheme.dimens.spacingHuge,
                    end = ZcashTheme.dimens.spacingHuge
                )
        )
    }
}

@Composable
private fun AboutTopAppBar(
    onBack: () -> Unit,
    versionInfo: VersionInfo,
    configInfo: ConfigInfo
) {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.about_title).uppercase(),
        backText = stringResource(id = R.string.about_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.about_back_content_description),
        onBack = onBack,
        regularActions = {
            if (versionInfo.isDebuggable) {
                DebugMenu(versionInfo, configInfo)
            }
        }
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
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val logoContentDescription = stringResource(R.string.about_app_logo_content_description)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = logoContentDescription
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.zashi_logo_without_text),
                contentDescription = null,
                Modifier
                    .height(ZcashTheme.dimens.inScreenZcashLogoHeight)
                    .width(ZcashTheme.dimens.inScreenZcashLogoWidth)
            )
            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))
            Image(
                painter = painterResource(id = R.drawable.zashi_text_logo),
                contentDescription = null,
                modifier = Modifier.height(ZcashTheme.dimens.inScreenZcashTextLogoHeight)
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text = stringResource(
                R.string.about_version_format,
                versionInfo.versionName,
                versionInfo.versionCode
            ),
            style = ZcashTheme.typography.primary.titleSmall
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text = stringResource(id = R.string.about_description),
            color = ZcashTheme.colors.aboutTextColor,
            style = ZcashTheme.extendedTypography.aboutText
        )
    }
}
