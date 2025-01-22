package co.electriccoin.zcash.ui.screen.transactionhistory.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun TransactionHistoryWidgetHeader(
    state: TransactionHistoryWidgetHeaderState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = state.title.getValue(),
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        ZashiButton(
            state = state.button,
            colors = ZashiButtonDefaults.tertiaryColors(),
            style = ZashiTypography.textSm,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            shape = CircleShape
        )
    }
}

data class TransactionHistoryWidgetHeaderState(
    val title: StringResource,
    val button: ButtonState
)

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    BlankSurface {
        TransactionHistoryWidgetHeader(
            TransactionHistoryWidgetHeaderState(
                title = stringRes("Transactions"),
                button = ButtonState(
                    text = stringRes("See All"),
                    trailingIcon = R.drawable.ic_chevron_right_small,
                    onClick = {}
                )
            )
        )
    }
}
