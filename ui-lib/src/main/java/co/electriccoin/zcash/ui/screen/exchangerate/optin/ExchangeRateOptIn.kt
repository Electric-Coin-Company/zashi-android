package co.electriccoin.zcash.ui.screen.exchangerate.optin

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiTextButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.exchangerate.BaseExchangeRateOptIn

@Composable
fun ExchangeRateOptIn(
    onEnabledClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseExchangeRateOptIn(
        onDismiss = onDismiss,
        content = {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.exchange_rate_opt_in_description),
                color = ZashiColors.Text.textTertiary,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            InfoItem(
                modifier = Modifier,
                image = R.drawable.ic_exchange_rate_info_1,
                title = stringResource(R.string.exchange_rate_info_title_1),
                subtitle = stringResource(R.string.exchange_rate_info_subtitle_1),
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoItem(
                modifier = Modifier,
                image = R.drawable.ic_exchange_rate_info_2,
                title = stringResource(R.string.exchange_rate_info_title_2),
                subtitle = stringResource(R.string.exchange_rate_info_subtitle_2),
            )
        },
        footer = {
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.exchange_rate_opt_in_enable),
                onClick = onEnabledClick,
                colors = ZashiButtonDefaults.primaryColors()
            )
            ZashiTextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(R.string.exchange_rate_opt_in_skip),
                    style = ZashiTypography.textMd,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun InfoItem(
    @DrawableRes image: Int,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Image(
            painter = painterResource(image),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun CurrencyConversionOptInPreview() =
    ZcashTheme {
        BlankSurface {
            ExchangeRateOptIn(onEnabledClick = {}, onDismiss = {})
        }
    }
