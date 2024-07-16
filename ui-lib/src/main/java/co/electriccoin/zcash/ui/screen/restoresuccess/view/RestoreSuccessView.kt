package co.electriccoin.zcash.ui.screen.restoresuccess.view

import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.LabeledCheckBox
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.restoresuccess.viewmodel.RestoreSuccessViewModel

@Composable
fun RestoreSuccessView(
    onPositiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    val viewModel by activity.viewModels<RestoreSuccessViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    RestoreSuccessViewInternal(
        modifier = modifier,
        state =
            state.copy(
                onPositiveClick = {
                    state.onPositiveClick()
                    onPositiveClick()
                }
            )
    )
}

@Suppress("LongMethod")
@Composable
private fun RestoreSuccessViewInternal(
    state: RestoreSuccessViewState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = ZcashTheme.dimens.spacingBig,
                    top = 118.dp,
                    end = ZcashTheme.dimens.spacingBig,
                    bottom = 54.dp,
                ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.restoring_initial_dialog_title),
            style = ZcashTheme.typography.secondary.headlineMedium
        )
        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        Image(
            painter = painterResource(id = R.drawable.img_success_dialog),
            contentDescription = stringResource(id = R.string.restoring_initial_dialog_subtitle),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(id = R.string.restoring_initial_dialog_subtitle),
            textAlign = TextAlign.Center,
            style = ZcashTheme.typography.secondary.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        Text(
            modifier = Modifier.padding(start = ZcashTheme.dimens.spacingDefault),
            text = stringResource(id = R.string.restoring_initial_dialog_description),
            style = ZcashTheme.typography.secondary.bodySmall,
        )
        Spacer(Modifier.height(28.dp))
        LabeledCheckBox(
            modifier = Modifier.fillMaxWidth(),
            checked = state.isKeepScreenOnChecked,
            onCheckedChange = { state.onCheckboxClick() },
            text = stringResource(id = R.string.restoring_initial_dialog_checkbox)
        )
        Spacer(Modifier.height(28.dp))
        Text(
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingMid),
            text =
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.restoring_initial_dialog_note_part_1))
                    }
                    append(" ")
                    append(stringResource(id = R.string.restoring_initial_dialog_note_part_2))
                },
            style = ZcashTheme.extendedTypography.footnote,
        )
        Spacer(Modifier.height(ZcashTheme.dimens.spacingBig))
        Spacer(Modifier.weight(1f))
        PrimaryButton(
            modifier = Modifier.padding(horizontal = 30.dp),
            onClick = state.onPositiveClick,
            text = stringResource(id = R.string.restoring_initial_dialog_positive_button)
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
        RestoreSuccessViewInternal(
            modifier = Modifier.fillMaxSize(),
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
