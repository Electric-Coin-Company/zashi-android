package co.electriccoin.zcash.ui.screen.securitywarning.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.LabeledCheckBox
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture

@Preview
@Composable
private fun SecurityWarningPreview() {
    ZcashTheme(forceDarkMode = false) {
        SecurityWarning(
            versionInfo = VersionInfoFixture.new(),
            onBack = {},
            onAcknowledged = {},
            onConfirm = {},
        )
    }
}

@Preview
@Composable
private fun SecurityWarningDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        SecurityWarning(
            versionInfo = VersionInfoFixture.new(),
            onBack = {},
            onAcknowledged = {},
            onConfirm = {},
        )
    }
}

@Composable
fun SecurityWarning(
    versionInfo: VersionInfo,
    onBack: () -> Unit,
    onAcknowledged: (Boolean) -> Unit,
    onConfirm: () -> Unit,
) {
    BlankBgScaffold(
        topBar = { SecurityWarningTopAppBar(onBack = onBack) },
    ) { paddingValues ->
        SecurityWarningContent(
            versionInfo = versionInfo,
            onAcknowledged = onAcknowledged,
            onConfirm = onConfirm,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                        end = ZcashTheme.dimens.screenHorizontalSpacingBig
                    )
                    .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun SecurityWarningTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        }
    )
}

@Composable
private fun SecurityWarningContent(
    versionInfo: VersionInfo,
    onAcknowledged: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopScreenLogoTitle(
            title = stringResource(R.string.security_warning_header),
            logoContentDescription = stringResource(R.string.zcash_logo_content_description)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        SecurityWarningContentText(
            versionInfo = versionInfo
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        val checkedState = rememberSaveable { mutableStateOf(false) }
        Row(Modifier.fillMaxWidth()) {
            LabeledCheckBox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    onAcknowledged(it)
                },
                text = stringResource(R.string.security_warning_acknowledge),
                checkBoxTestTag = SecurityScreenTag.ACKNOWLEDGE_CHECKBOX_TAG
            )
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        ZashiButton(
            onClick = onConfirm,
            text = stringResource(R.string.security_warning_confirm).uppercase(),
            enabled = checkedState.value,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
fun SecurityWarningContentText(versionInfo: VersionInfo) {
    Column {
        Text(
            text = stringResource(id = R.string.security_warning_text, versionInfo.versionName),
            color = ZcashTheme.colors.textPrimary,
            style = ZcashTheme.extendedTypography.securityWarningText
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        val textPart1 = stringResource(R.string.security_warning_text_footnote_part_1)
        val textPart2 = stringResource(R.string.security_warning_text_footnote_part_2)

        Text(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(textPart1)
                    }
                    append(textPart2)
                },
            color = ZcashTheme.colors.textPrimary,
            style = ZcashTheme.extendedTypography.footnote,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
