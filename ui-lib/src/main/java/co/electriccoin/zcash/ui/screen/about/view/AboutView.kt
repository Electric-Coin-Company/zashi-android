package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.ConfigInfoFixture
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo
import co.electriccoin.zcash.ui.screen.support.model.ConfigInfo

@Preview
@Composable
fun AboutPreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            About(
                versionInfo = VersionInfoFixture.new(),
                configInfo = ConfigInfoFixture.new(),
                goBack = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    versionInfo: VersionInfo,
    configInfo: ConfigInfo,
    goBack: () -> Unit
) {
    Scaffold(topBar = {
        AboutTopAppBar(onBack = goBack)
    }) { paddingValues ->
        AboutMainContent(
            versionInfo,
            configInfo,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                    start = ZcashTheme.dimens.spacingDefault,
                    end = ZcashTheme.dimens.spacingDefault
                )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AboutTopAppBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.about_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.about_back_content_description)
                )
            }
        }
    )
}

@Composable
fun AboutMainContent(
    versionInfo: VersionInfo,
    configInfo: ConfigInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Icon(painterResource(id = R.drawable.ic_launcher_adaptive_foreground), contentDescription = null)
        Text(stringResource(id = R.string.app_name))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Header(stringResource(id = R.string.about_version_header))
        Body(stringResource(R.string.about_version_format, versionInfo.versionName, versionInfo.versionCode))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Header(stringResource(id = R.string.about_build_header))
        Body(gitSha)

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        configInfo.configurationUpdatedAt?.let { updatedAt ->
            Header(stringResource(id = R.string.about_build_configuration))
            Body(updatedAt.toString())
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Header(stringResource(id = R.string.about_legal_header))
        Body(stringResource(id = R.string.about_legal_info))
    }
}
