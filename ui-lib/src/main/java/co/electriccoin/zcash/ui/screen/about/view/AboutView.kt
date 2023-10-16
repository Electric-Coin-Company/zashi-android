package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo

@Preview("About")
@Composable
private fun AboutPreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            About(
                versionInfo = VersionInfoFixture.new(),
                goBack = {}
            )
        }
    }
}

@Composable
fun About(
    versionInfo: VersionInfo,
    goBack: () -> Unit
) {
    Scaffold(topBar = {
        AboutTopAppBar(onBack = goBack)
    }) { paddingValues ->
        AboutMainContent(
            versionInfo,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun AboutTopAppBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = R.string.about_title
                ),
                style = ZcashTheme.typography.primary.titleSmall,
                color = ZcashTheme.colors.screenTitleColor
            )
        },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.about_back_content_description)
                    )
                }
                Text(
                    text = stringResource(id = R.string.about_back_label),
                    style = ZcashTheme.typography.primary.bodyMedium
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.about_settings_icon_content_description)
                )
            }
        }
    )
}

@Composable
fun AboutMainContent(
    versionInfo: VersionInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(id = R.drawable.zashi_logo_without_text),
                stringResource(R.string.zcash_logo_content_description),
                Modifier
                    .height(ZcashTheme.dimens.zcashLogoHeight)
                    .width(ZcashTheme.dimens.zcashLogoWidth)
            )
            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingDefault))
            Image(
                painterResource(
                    id = R.drawable.zashi_text_logo
                ),
                contentDescription = stringResource(R.string.about_zashi_text_logo_content_description),
                modifier = Modifier.height(ZcashTheme.dimens.zcashTextLogoHeight)
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
