package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Composable
fun SwapTextField(
    state: SwapTextFieldState,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        if (isFocused) {
            ZashiColors.Dropdowns.Focused.stroke
        } else {
            ZashiColors.Surfaces.strokeSecondary
        }
    )

    Surface(
        modifier = modifier,
        color = ZashiColors.Surfaces.bgPrimary,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(width = 1.dp, color = borderColor)
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                if (state.primaryText.value
                        .getValue()
                        .isNotEmpty()
                ) {
                    Text(
                        text = state.symbol.getValue(),
                        style = ZashiTypography.header4,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textPrimary,
                    )
                }
                ZashiTextField(
                    state = state.primaryText,
                    modifier = Modifier.weight(1f),
                    textStyle = ZashiTypography.header4.copy(fontWeight = FontWeight.SemiBold),
                    placeholder = {
                        Text(
                            text = state.primaryPlaceholder.getValue(),
                            style = ZashiTypography.header4,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    colors =
                        ZashiTextFieldDefaults.defaultColors(
                            textColor = ZashiColors.Text.textPrimary,
                            placeholderColor = ZashiColors.Text.textSecondary,
                            borderColor = Color.Unspecified,
                            focusedBorderColor = Color.Unspecified,
                            containerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                        )
                )
                Spacer(4.dp)
                SwapToken(
                    state = state.token
                )
            }
            Spacer(6.dp)
            Row(
                verticalAlignment = CenterVertically
            ) {
                Text(
                    text = state.secondaryText.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Text.textTertiary
                )
                Spacer(4.dp)
                Image(
                    modifier = Modifier.clickable(onClick = state.onSwapChange),
                    painter = painterResource(R.drawable.ic_swap_recipient),
                    contentDescription = null
                )
                Spacer(1f)
                Text(
                    text = state.exchangeRate.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Text.textTertiary
                )
            }
        }
    }
}

@Immutable
data class SwapTextFieldState(
    val title: StringResource,
    val symbol: StringResource,
    val token: SwapTokenState,
    val primaryText: TextFieldState,
    val primaryPlaceholder: StringResource,
    val secondaryText: StringResource,
    val exchangeRate: StringResource,
    val onSwapChange: () -> Unit
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapTextField(
                state =
                    SwapTextFieldState(
                        token = SwapTokenState(stringRes("USDT")),
                        title = stringRes("Recipient gets"),
                        symbol = stringRes("$"),
                        primaryText = TextFieldState(value = stringRes("")) {},
                        primaryPlaceholder = stringResByDynamicCurrencyNumber(0, "$"),
                        secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                        exchangeRate = stringResByDynamicCurrencyNumber(100, "$"),
                        onSwapChange = {},
                    )
            )
        }
    }
