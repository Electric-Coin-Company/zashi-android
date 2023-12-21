package co.electriccoin.zcash.ui.screen.securitywarning.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.CheckBox
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopScreenLogoTitle
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.VersionInfoFixture

@Preview("Security Warning")
@Composable
private fun SecurityWarningPreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SecurityWarning(
                snackbarHostState = SnackbarHostState(),
                versionInfo = VersionInfoFixture.new(),
                onBack = {},
                onAcknowledged = {},
                onPrivacyPolicy = {},
                onConfirm = {},
            )
        }
    }
}

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

@Composable
@Suppress("LongParameterList")
fun SecurityWarning(
    snackbarHostState: SnackbarHostState,
    versionInfo: VersionInfo,
    onBack: () -> Unit,
    onAcknowledged: (Boolean) -> Unit,
    onPrivacyPolicy: () -> Unit,
    onConfirm: () -> Unit,
) {
    Scaffold(
        topBar = { SecurityWarningTopAppBar(onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        SecurityWarningContent(
            versionInfo = versionInfo,
            onPrivacyPolicy = onPrivacyPolicy,
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
        backText = stringResource(R.string.security_warning_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.security_warning_back_content_description),
        onBack = onBack,
    )
}

@Composable
private fun SecurityWarningContent(
    versionInfo: VersionInfo,
    onPrivacyPolicy: () -> Unit,
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
            versionInfo = versionInfo,
            onPrivacyPolicy = onPrivacyPolicy
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingDefault))

        val checkedState = rememberSaveable { mutableStateOf(false) }
        CheckBox(
            modifier =
                Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth(),
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                onAcknowledged(it)
            },
            text = stringResource(R.string.security_warning_acknowledge),
            checkBoxTestTag = SecurityScreenTag.ACKNOWLEDGE_CHECKBOX_TAG
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            onClick = onConfirm,
            text = stringResource(R.string.security_warning_confirm).uppercase(),
            enabled = checkedState.value
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
fun SecurityWarningContentText(
    versionInfo: VersionInfo,
    onPrivacyPolicy: () -> Unit,
) {
    val textPart1 = stringResource(R.string.security_warning_text_part_1, versionInfo.versionName)
    val textPart2 = stringResource(R.string.security_warning_text_part_2)
    ClickableText(
        text =
            buildAnnotatedString {
                append(textPart1)
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(textPart2)
                }
                append(stringResource(R.string.security_warning_text_part_3))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.security_warning_text_part_4))
                }
                append(stringResource(R.string.security_warning_text_part_5))
            },
        style = ZcashTheme.extendedTypography.securityWarningText,
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag(SecurityScreenTag.WARNING_TEXT_TAG),
        onClick = { letterOffset ->
            // Call the callback only if user clicked the underlined part
            if (letterOffset >= textPart1.length && letterOffset <= (textPart1.length + textPart2.length)) {
                onPrivacyPolicy()
            }
        }
    )
}
