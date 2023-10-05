package co.electriccoin.zcash.ui.screen.securitywarning.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.CheckBox
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture
import co.electriccoin.zcash.ui.screen.about.model.VersionInfo

@Preview("Security Warning")
@Composable
private fun SecurityWarningPreview() {
    ZcashTheme {
        GradientSurface {
            SecurityWarning(
                versionInfo = VersionInfoFixture.new(),
                onBack = {}
            )
        }
    }
}

@Composable
fun SecurityWarning(
    versionInfo: VersionInfo,
    onBack: () -> Unit
) {
    val checkedState = remember { mutableStateOf(true) }
    Scaffold(topBar = {
        SecurityWarningTopAppBar(onBack = onBack)
    }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = ZcashTheme.dimens.spacingHuge,
                    end = ZcashTheme.dimens.spacingHuge
                )
                .verticalScroll(rememberScrollState()),

        ) {
            Image(
                painterResource(id = R.drawable.zashi_logo_without_text),
                stringResource(R.string.zcash_logo_content_description),
                Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(ZcashTheme.dimens.spacingXlarge))

            Text(
                text = stringResource(R.string.security_warning_header),
                style = ZcashTheme.typography.secondary.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

            Text(
                text = stringResource(R.string.security_warning_text, versionInfo.versionName),
                style = ZcashTheme.extendedTypography.securityWarningText
            )

            Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

            CheckBox(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
                text = stringResource(R.string.security_warning_acknowledge),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
            )

            PrimaryButton(
                onClick = {},
                text = stringResource(R.string.security_warning_confirm).uppercase(),
                Modifier.padding(bottom = ZcashTheme.dimens.spacingHuge)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SecurityWarningTopAppBar(
    onBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.security_warning_back).uppercase(),
                style = ZcashTheme.typography.primary.bodyMedium
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.security_warning_back_content_description)
                )
            }
        }
    )
}
