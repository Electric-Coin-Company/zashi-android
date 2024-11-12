package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding

@Suppress("LongMethod")
@Composable
internal fun BaseExchangeRateOptIn(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    footer: @Composable ColumnScope.() -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .scaffoldPadding(paddingValues)
        ) {
            Button(
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(40.dp),
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ZashiColors.Btns.Tertiary.btnTertiaryBg
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_exchange_rate_close),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(ZashiColors.Btns.Tertiary.btnTertiaryFg)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
            ) {
                Image(painter = painterResource(R.drawable.exchange_rate), contentDescription = "")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.exchange_rate_opt_in_subtitle),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.header6,
                    fontWeight = FontWeight.SemiBold
                )
                content()

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.weight(1f))

                Row {
                    Image(
                        painter = painterResource(R.drawable.ic_exchange_rate_info),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(ZashiColors.Text.textTertiary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.exchange_rate_opt_in_note),
                        color = ZashiColors.Text.textTertiary,
                        style = ZashiTypography.textXs
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            footer()
        }
    }
}
