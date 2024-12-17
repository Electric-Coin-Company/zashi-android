package co.electriccoin.zcash.ui.screen.exchangerate.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.screen.exchangerate.SecondaryCard

@Suppress("LongMethod")
@Composable
fun StyledExchangeOptIn(
    state: ExchangeRateState.OptIn,
    modifier: Modifier = Modifier
) {
    SecondaryCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, bottom = 20.dp)
        ) {
            Row {
                Image(
                    modifier = Modifier.padding(top = 20.dp),
                    painter = painterResource(Icon),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = stringResource(R.string.exchange_rate_opt_in_title),
                        color = ZashiColors.Text.textTertiary,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.exchange_rate_opt_in_subtitle),
                        color = ZashiColors.Text.textPrimary,
                        fontSize = 16.sp,
                        style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    modifier = Modifier.padding(top = 4.dp, end = 8.dp),
                    onClick = state.onDismissClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_exchange_rate_unavailable_dialog_close),
                        contentDescription = null,
                        tint = ZashiColors.HintTooltips.defaultFg
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ZashiButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp),
                onClick = state.onPrimaryClick,
                colors = ZashiButtonDefaults.tertiaryColors(),
                text = stringResource(R.string.exchange_rate_opt_in_primary_btn),
            ) { scope ->
                Text(
                    text = stringResource(R.string.exchange_rate_opt_in_primary_btn),
                    style = ZcashTheme.typography.primary.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 14.sp
                )
                scope.Loading()
            }
        }
    }
}

private val Icon: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.ic_exchange_rate_opt_in
        } else {
            R.drawable.ic_exchange_rate_opt_in_light
        }

@Suppress("UnusedPrivateMember")
@Composable
@PreviewScreens
private fun ExchangeRateOptInPreview() =
    ZcashTheme {
        BlankSurface {
            StyledExchangeOptIn(
                modifier = Modifier.fillMaxWidth(),
                state = ExchangeRateState.OptIn(onDismissClick = {})
            )
        }
    }
