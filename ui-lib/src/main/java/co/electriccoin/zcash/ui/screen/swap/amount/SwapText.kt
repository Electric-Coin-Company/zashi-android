package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Composable
fun SwapText(
    state: SwapTextState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ZashiColors.Utility.Gray.utilityGray50,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = state.title.getValue(),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textSecondary
            )
            Spacer(4.dp)
            Row(
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = state.text.getValue(),
                    style = ZashiTypography.header4,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textTertiary
                )
                Spacer(1f)
                SwapToken(
                    state = state.token
                )
            }
            Spacer(6.dp)
            Text(
                text = state.secondaryText.getValue(),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textTertiary
            )
        }
    }
}

@Immutable
data class SwapTextState(
    val token: SwapTokenState,
    val title: StringResource,
    val text: StringResource,
    val secondaryText: StringResource,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapText(
                state =
                    SwapTextState(
                        token =
                            SwapTokenState(
                                stringRes("ZEC")
                            ),
                        title = stringRes("You pay"),
                        text = stringResByDynamicCurrencyNumber(101, "$"),
                        secondaryText = stringResByDynamicCurrencyNumber(2.47123, "ZEC")
                    )
            )
        }
    }
