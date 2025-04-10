package co.electriccoin.zcash.ui.screen.home.transparentbalance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCard
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentBalanceInfoView(
    state: TransparentBalanceInfoState?,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        sheetState = sheetState,
        state = state,
    ) {
        Content(it)
    }
}

@Composable
private fun Content(state: TransparentBalanceInfoState) {
    Column(
        modifier =
            Modifier
                .padding(horizontal = 24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_info_shield),
            contentDescription = null
        )
        Spacer(12.dp)
        Text(
            stringResource(R.string.home_info_transparent_title),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(12.dp)
        Text(
            stringResource(R.string.home_info_transparent_subtitle),
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textMd
        )
        Spacer(24.dp)
        Text(
            stringResource(R.string.home_info_transparent_message),
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textMd
        )
        Spacer(32.dp)
        ZashiCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding =
                PaddingValues(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
            borderColor = ZashiColors.Surfaces.strokeSecondary
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_info_transparent_subheader),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.Medium
                )
                Spacer(4.dp)
                Image(
                    painter = painterResource(R.drawable.ic_transparent_small),
                    contentDescription = null
                )
            }
            Spacer(4.dp)
            Text(
                text =
                    stringResource(
                        R.string.home_message_transparent_balance_subtitle,
                        stringRes(state.transparentAmount).getValue()
                    ),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(24.dp)
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state = state.secondaryButton,
            colors = ZashiButtonDefaults.secondaryColors()
        )
        Spacer(4.dp)
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state = state.primaryButton
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        TransparentBalanceInfoView(
            state =
                TransparentBalanceInfoState(
                    onBack = {},
                    primaryButton =
                        ButtonState(
                            text = stringRes("Remind me later"),
                            onClick = {}
                        ),
                    secondaryButton =
                        ButtonState(
                            text = stringRes("Shield"),
                            onClick = {}
                        ),
                    transparentAmount = Zatoshi(0)
                )
        )
    }
