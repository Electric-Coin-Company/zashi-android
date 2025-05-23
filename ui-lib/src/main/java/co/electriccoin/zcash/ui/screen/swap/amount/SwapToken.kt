package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun SwapToken(
    state: SwapTokenState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = ZashiColors.Surfaces.bgPrimary,
        border = BorderStroke(.33.dp, ZashiColors.Surfaces.strokeSecondary),
        shadowElevation = 4.dp
    ) {
        Row(
           modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = state.ticker.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Immutable
data class SwapTokenState(
    val ticker: StringResource
)

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    BlankSurface {
        SwapToken(
            state = SwapTokenState(
                stringRes("USDT")
            )
        )
    }
}
