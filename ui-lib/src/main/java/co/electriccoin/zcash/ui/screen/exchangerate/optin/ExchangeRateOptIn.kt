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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.exchangerate.BaseExchangeRateOptIn
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiButton
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiButtonDefaults
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiTextButton
import co.electriccoin.zcash.ui.util.PreviewScreens

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
                color = ZcashTheme.zashiColors.textTertiary,
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
                onClick = onEnabledClick,
                colors = ZashiButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(R.string.exchange_rate_opt_in_enable)
                )
            }
            ZashiTextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss,
            ) {
                Text(text = stringResource(R.string.exchange_rate_opt_in_skip))
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
                color = ZcashTheme.zashiColors.textPrimary,
                fontSize = 16.sp,
                style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = ZcashTheme.zashiColors.textTertiary,
                fontSize = 14.sp,
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
