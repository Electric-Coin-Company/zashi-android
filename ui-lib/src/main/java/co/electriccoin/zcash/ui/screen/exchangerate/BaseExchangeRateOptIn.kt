package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Suppress("LongMethod")
@Composable
internal fun BaseExchangeRateOptIn(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
) {
    Scaffold {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = 24.dp,
                        top = it.calculateTopPadding() + 12.dp,
                        end = 24.dp,
                        bottom = it.calculateBottomPadding() + 24.dp
                    )
        ) {
            Button(
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(40.dp),
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ZcashTheme.zashiColors.btnPrimaryBgDisabled
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_exchange_rate_close),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(ZcashTheme.zashiColors.btnTertiaryFg)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
            ) {
                Image(painter = painterResource(Image), contentDescription = "")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Currency Conversion",
                    color = ZcashTheme.zashiColors.textPrimary,
                    fontSize = 24.sp,
                    style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                )
                content()

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Image(
                        painter = painterResource(R.drawable.ic_exchange_rate_info),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(ZcashTheme.zashiColors.textPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.exchange_rate_opt_in_note),
                        color = ZcashTheme.zashiColors.textTertiary,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            footer()
        }
    }
}

private val Image: Int
    @DrawableRes
    @Composable
    get() =
        if (isSystemInDarkTheme()) {
            R.drawable.exchange_rate
        } else {
            R.drawable.exchange_rate_light
        }
