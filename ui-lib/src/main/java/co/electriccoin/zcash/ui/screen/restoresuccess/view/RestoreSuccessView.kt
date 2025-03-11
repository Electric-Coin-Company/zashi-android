package co.electriccoin.zcash.ui.screen.restoresuccess.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.GradientBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiCheckbox
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun RestoreSuccess(state: RestoreSuccessViewState) {
    GradientBgScaffold(
        startColor = ZashiColors.Utility.WarningYellow.utilityOrange100,
        endColor = ZashiColors.Surfaces.bgPrimary,
    ) { paddingValues ->
        RestoreSuccessContent(
            state = state,
            modifier =
                Modifier
                    .fillMaxSize()
                    .scaffoldPadding(paddingValues)
                    .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun RestoreSuccessContent(
    state: RestoreSuccessViewState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.img_success_dialog),
            contentDescription = stringResource(id = R.string.restore_success_subtitle),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.restore_success_title),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.restore_success_subtitle),
            textAlign = TextAlign.Center,
            style = ZashiTypography.textMd,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.restore_success_description),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary,
        )

        val bulletString = "\u2022  "
        val bulletTextStyle = ZashiTypography.textSm
        val bulletTextMeasurer = rememberTextMeasurer()
        val bulletStringWidth =
            remember(bulletTextStyle, bulletTextMeasurer) {
                bulletTextMeasurer.measure(text = bulletString, style = bulletTextStyle).size.width
            }
        val bulletRestLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
        val bulletParagraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = bulletRestLine))

        Spacer(Modifier.height(4.dp))

        val bulletText1 = stringResource(R.string.restore_success_bullet_1)
        Text(
            text =
                buildAnnotatedString {
                    withStyle(style = bulletParagraphStyle) {
                        append(bulletString)
                        append(bulletText1)
                    }
                },
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary,
        )

        Spacer(Modifier.height(2.dp))

        val bulletText2 = stringResource(R.string.restore_success_bullet_2)
        Text(
            text =
                buildAnnotatedString {
                    withStyle(style = bulletParagraphStyle) {
                        append(bulletString)
                        append(bulletText2)
                    }
                },
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.restore_success_note_part_1))
                    }
                    append(" ")
                    append(stringResource(id = R.string.restore_success_note_part_2))
                },
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textPrimary,
        )

        Spacer(Modifier.height(14.dp))

        ZashiCheckbox(
            modifier = Modifier.align(Alignment.Start),
            isChecked = state.isKeepScreenOnChecked,
            onClick = state.onCheckboxClick,
            text = stringRes(R.string.restoring_initial_dialog_checkbox),
            style = ZashiTypography.textMd,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textPrimary,
        )

        Spacer(Modifier.height(14.dp))

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = state.onPositiveClick,
            text = stringResource(id = R.string.restore_success_button)
        )
    }
}

data class RestoreSuccessViewState(
    val isKeepScreenOnChecked: Boolean,
    val onCheckboxClick: () -> Unit,
    val onPositiveClick: () -> Unit,
)

@Composable
private fun RestoreSuccessViewPreview() =
    BlankSurface {
        RestoreSuccess(
            state =
                RestoreSuccessViewState(
                    isKeepScreenOnChecked = true,
                    onCheckboxClick = {},
                    onPositiveClick = {},
                )
        )
    }

@Preview(device = Devices.PIXEL_7_PRO)
@Composable
private fun RestoreSuccessViewPreviewLight() =
    ZcashTheme(false) {
        RestoreSuccessViewPreview()
    }

@Preview(device = Devices.PIXEL_7_PRO)
@Composable
private fun RestoreSuccessViewPreviewDark() =
    ZcashTheme(true) {
        RestoreSuccessViewPreview()
    }
