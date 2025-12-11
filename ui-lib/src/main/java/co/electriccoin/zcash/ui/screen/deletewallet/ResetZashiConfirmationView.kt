package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetZashiConfirmationView(state: ResetZashiConfirmationState?) {
    ZashiScreenModalBottomSheet(state = state, content = { state, contentPadding ->
        Content(
            modifier = Modifier.weight(1f, false),
            state = state,
            contentPadding = contentPadding
        )
    })
}

@Composable
private fun Content(
    state: ResetZashiConfirmationState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = contentPadding.calculateBottomPadding()
                ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_reset_zashi_warning),
            contentDescription = null
        )
        Spacer(8.dp)
        Text(
            text = stringResource(R.string.delete_wallet_confirmation_title),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(8.dp)
        Text(
            text = stringResource(R.string.delete_wallet_confirmation_subtitle),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
            textAlign = TextAlign.Center
        )
        Spacer(28.dp)
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state =
                ButtonState(
                    text = stringRes(R.string.delete_wallet_confirmation_button),
                    onClick = state.onConfirm
                ),
            defaultPrimaryColors = ZashiButtonDefaults.destructive1Colors()
        )
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state =
                ButtonState(
                    text = stringRes(R.string.delete_wallet_confirmation_cancel),
                    onClick = state.onCancel
                ),
            defaultPrimaryColors = ZashiButtonDefaults.primaryColors()
        )
    }
}

@PreviewScreens
@Composable
private fun ResetZashiConfirmationPreview() =
    ZcashTheme {
        ResetZashiConfirmationView(
            state =
                ResetZashiConfirmationState(
                    onBack = {},
                    onConfirm = {},
                    onCancel = {}
                )
        )
    }
