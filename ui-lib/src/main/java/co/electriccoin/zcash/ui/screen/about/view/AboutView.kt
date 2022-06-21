package co.electriccoin.zcash.ui.screen.about.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.build.gitSha
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo

@Preview
@Composable
fun AboutPreview() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            About(versionInfo = VersionInfoFixture.new(), goBack = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    versionInfo: VersionInfo,
    goBack: () -> Unit
) {
    Scaffold(topBar = {
        AboutTopAppBar(onBack = goBack)
    }) { paddingValues ->
        AboutMainContent(
            paddingValues,
            versionInfo
        )
    }
}

@Composable
private fun AboutTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
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
fun AboutMainContent(paddingValues: PaddingValues, versionInfo: VersionInfo) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Icon(painterResource(id = R.drawable.ic_launcher_adaptive_foreground), contentDescription = null)
        Text(stringResource(id = R.string.app_name))

        Spacer(modifier = Modifier.height(24.dp))

        Header(stringResource(id = R.string.about_version_header))
        Body(stringResource(R.string.about_version_format, versionInfo.versionName, versionInfo.versionCode))

        Spacer(modifier = Modifier.height(24.dp))

        Header(stringResource(id = R.string.about_build_header))
        Body(gitSha)

        Spacer(modifier = Modifier.height(24.dp))

        Header(stringResource(id = R.string.about_legal_header))
        Body(stringResource(id = R.string.about_legal_info))
    }
}
