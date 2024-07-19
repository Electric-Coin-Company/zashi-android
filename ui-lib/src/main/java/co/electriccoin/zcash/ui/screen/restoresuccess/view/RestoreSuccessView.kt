package co.electriccoin.zcash.ui.screen.restoresuccess.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.GridBgScaffold
import co.electriccoin.zcash.ui.design.component.LabeledCheckBox
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Composable
fun RestoreSuccess(state: RestoreSuccessViewState) {
    GridBgScaffold { paddingValues ->
        RestoreSuccessContent(
            state = state,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
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
        modifier =
            modifier.then(
                Modifier.padding(
                    start = ZcashTheme.dimens.screenHorizontalSpacingBig,
                    end = ZcashTheme.dimens.screenHorizontalSpacingBig
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Text(
            text = stringResource(id = R.string.restore_success_title),
            style = ZcashTheme.typography.secondary.headlineMedium
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Image(
            painter = painterResource(id = R.drawable.img_success_dialog),
            contentDescription = stringResource(id = R.string.restore_success_subtitle),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Text(
            text = stringResource(id = R.string.restore_success_subtitle),
            textAlign = TextAlign.Center,
            style = ZcashTheme.typography.secondary.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        Text(
            text = stringResource(id = R.string.restore_success_description),
            style = ZcashTheme.typography.secondary.bodySmall,
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        LabeledCheckBox(
            modifier = Modifier.align(Alignment.Start),
            checked = state.isKeepScreenOnChecked,
            onCheckedChange = { state.onCheckboxClick() },
            text = stringResource(id = R.string.restoring_initial_dialog_checkbox)
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingLarge))

        Text(
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.restore_success_note_part_1))
                    }
                    append(" ")
                    append(stringResource(id = R.string.restore_success_note_part_2))
                },
            style = ZcashTheme.extendedTypography.footnote,
        )

        Spacer(Modifier.height(ZcashTheme.dimens.spacingBig))

        Spacer(Modifier.weight(1f))

        PrimaryButton(
            onClick = state.onPositiveClick,
            text = stringResource(id = R.string.restore_success_button)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))
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
