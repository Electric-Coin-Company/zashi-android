package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
fun ExchangeRateTorView(state: ExchangeRateTorState) {
    ZashiScreenModalBottomSheet(
        state = state,
    ) {
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.ic_tor_settings),
                contentDescription = null
            )
            Spacer(12.dp)
            Text(
                text = stringResource(R.string.exchange_rate_tor_opt_in_title),
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold,
                color = ZashiColors.Text.textPrimary
            )
            Spacer(4.dp)
            Text(
                text = stringResource(R.string.exchange_rate_tor_opt_in_description),
                style = ZashiTypography.textMd,
                color = ZashiColors.Text.textTertiary
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.negative,
                defaultPrimaryColors = ZashiButtonDefaults.secondaryColors()
            )
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.positive
            )
        }
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        ExchangeRateTorView(
            state =
                ExchangeRateTorState(
                    positive = ButtonState(stringRes("Positive")),
                    negative = ButtonState(stringRes("Negative")),
                    onBack = {}
                )
        )
    }
