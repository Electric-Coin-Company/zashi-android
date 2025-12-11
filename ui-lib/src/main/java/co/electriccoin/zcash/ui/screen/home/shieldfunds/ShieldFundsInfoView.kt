package co.electriccoin.zcash.ui.screen.home.shieldfunds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import cash.z.ecc.sdk.extension.typicalFee
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCard
import co.electriccoin.zcash.ui.design.component.ZashiCheckbox
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.util.CURRENCY_TICKER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShieldFundsInfoView(
    state: ShieldFundsInfoState?,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        state = state,
        sheetState = sheetState,
        content = { state, contentPadding ->
            Content(
                modifier =
                    Modifier
                        .weight(1f, false),
                state = state,
                contentPadding = contentPadding
            )
        }
    )
}

@Composable
private fun Content(
    state: ShieldFundsInfoState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = contentPadding.calculateBottomPadding()
                )
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
            stringResource(R.string.home_info_transparent_subtitle, CURRENCY_TICKER),
            color = ZashiColors.Text.textTertiary,
            style = ZashiTypography.textMd
        )
        Spacer(24.dp)
        Text(
            stringRes(
                R.string.home_info_transparent_message,
                stringRes(Zatoshi.typicalFee, HIDDEN),
                CURRENCY_TICKER
            ).getValue(),
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
                        stringRes(state.transparentAmount, HIDDEN).getValue(),
                        CURRENCY_TICKER
                    ),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(24.dp)
        ZashiCheckbox(state = state.checkbox)
        Spacer(24.dp)
        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            state = state.secondaryButton,
            defaultPrimaryColors = ZashiButtonDefaults.secondaryColors()
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
        ShieldFundsInfoView(
            state =
                ShieldFundsInfoState(
                    onBack = {},
                    primaryButton =
                        ButtonState(
                            text = stringRes("Remind me later"),
                            onClick = {}
                        ),
                    secondaryButton =
                        ButtonState(
                            text = stringRes("Not now"),
                            onClick = {}
                        ),
                    transparentAmount = Zatoshi(0),
                    checkbox =
                        CheckboxState(
                            title = stringRes(R.string.home_info_transparent_checkbox),
                            onClick = {},
                            isChecked = false
                        )
                )
        )
    }
