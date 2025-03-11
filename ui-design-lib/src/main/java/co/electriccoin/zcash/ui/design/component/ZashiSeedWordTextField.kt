package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiSeedWordTextField(
    prefix: String,
    state: SeedWordTextFieldState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ZashiTextField(
        modifier = modifier,
        innerModifier = Modifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        state =
            TextFieldState(
                value = state.value,
                onValueChange = state.onValueChange,
            ),
        textStyle = ZashiTypography.textMd,
        prefix = {
            Box(
                modifier =
                    Modifier
                        .size(22.dp)
                        .background(ZashiColors.Tags.tcCountBg, CircleShape)
                        .padding(end = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = prefix,
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Tags.tcCountFg,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        colors =
            ZashiTextFieldDefaults.defaultColors(
                containerColor = ZashiColors.Surfaces.bgSecondary,
                focusedContainerColor = ZashiColors.Surfaces.bgPrimary,
                focusedBorderColor = ZashiColors.Accordion.focusStroke
            )
    )
}

@Immutable
data class SeedWordTextFieldState(
    val value: StringResource,
    val isError: Boolean,
    // val isFocused: Boolean,
    // val onFocusChange: (Boolean) -> Unit,
    val onValueChange: (String) -> Unit
)

@Composable
@PreviewScreens
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiSeedWordTextField(
                prefix = "12",
                state =
                    SeedWordTextFieldState(
                        value = stringRes("asd"),
                        isError = false,
                        onValueChange = {},
                    )
            )
        }
    }
