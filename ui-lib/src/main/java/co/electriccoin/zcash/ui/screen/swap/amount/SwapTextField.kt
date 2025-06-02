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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiNumberTextField
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
fun SwapTextField(state: SwapTextFieldState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SwapTextFieldCard(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )

        if (state.isError) {
            val error = state.error ?: state.textField.errorString

            if (error.getValue().isNotEmpty()) {
                Spacer(8.dp)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Inputs.ErrorDefault.hint,
                    textAlign = TextAlign.End
                )
                Spacer(8.dp)
            }
        }
    }
}

@Composable
private fun SwapTextFieldCard(
    state: SwapTextFieldState,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        when {
            state.isError -> ZashiColors.Inputs.ErrorDefault.stroke
            isFocused -> ZashiColors.Dropdowns.Focused.stroke
            else -> ZashiColors.Surfaces.strokeSecondary
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
                if (state.textFieldPrefix != null && !state.textField.text.isEmpty()) {
                    Text(
                        text = state.textFieldPrefix.getValue(),
                        style = ZashiTypography.header4,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textPrimary,
                    )
                }
                ZashiNumberTextField(
                    state = state.textField,
                    modifier = Modifier.weight(1f),
                    textStyle = ZashiTypography.header4.copy(fontWeight = FontWeight.SemiBold),
                    placeholder = {
                        Text(
                            text = state.textFieldPlaceholder.getValue(),
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
                            errorTextColor = ZashiColors.Text.textPrimary,
                            errorHintColor = ZashiColors.Inputs.Default.hint,
                            errorBorderColor = Color.Unspecified,
                            errorContainerColor = Color.Transparent,
                            errorPlaceholderColor = ZashiColors.Inputs.Default.text,
                        )
                )
                Spacer(4.dp)
                SwapAssetCard(
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
                if (state.totalBalance != null) {
                    Text(
                        text = state.totalBalance.getValue(),
                        style = ZashiTypography.textSm,
                        fontWeight = FontWeight.Medium,
                        color = if (state.isError) ZashiColors.Text.textError else ZashiColors.Text.textTertiary
                    )
                }
            }
        }
    }
}

@Immutable
data class SwapTextFieldState(
    val title: StringResource,
    val error: StringResource?,
    val token: SwapAssetCardState,
    val textFieldPrefix: StringResource?,
    val textField: NumberTextFieldState,
    val textFieldPlaceholder: StringResource,
    val secondaryText: StringResource,
    val totalBalance: StringResource?,
    val onSwapChange: () -> Unit
) {
    val isError = error != null || textField.isError
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapTextField(
                state =
                    SwapTextFieldState(
                        token = SwapAssetCardState(stringRes("USDT"), null, null),
                        title = stringRes("Recipient gets"),
                        textFieldPrefix = stringRes("$"),
                        textField = NumberTextFieldState {},
                        textFieldPlaceholder = stringResByDynamicCurrencyNumber(0, "$"),
                        secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                        totalBalance = stringResByDynamicCurrencyNumber(1000, "$"),
                        error = null,
                        onSwapChange = {},
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun ErrorPreview() =
    ZcashTheme {
        BlankSurface {
            SwapTextField(
                state =
                    SwapTextFieldState(
                        token = SwapAssetCardState(stringRes("USDT"), null, null),
                        title = stringRes("Recipient gets"),
                        textFieldPrefix = stringRes("$"),
                        textField = NumberTextFieldState {},
                        textFieldPlaceholder = stringResByDynamicCurrencyNumber(0, "$"),
                        secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                        totalBalance = stringResByDynamicCurrencyNumber(100, "$"),
                        error = stringRes("Error"),
                        onSwapChange = {},
                    )
            )
        }
    }
