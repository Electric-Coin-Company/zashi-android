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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiSeedWordTextField(
    prefix: String,
    state: SeedWordTextFieldState,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    ZashiTextField(
        modifier = modifier,
        innerModifier = innerModifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        state =
            EnhancedTextFieldState(
                innerState = InnerTextFieldState(
                    value = stringRes(state.innerState.value),
                    selection = state.innerState.selection
                ),
                onValueChange = {
                    state.onValueChange(
                        SeedWordInnerTextFieldState(
                            value = it.value.getString(context, locale),
                            selection = it.selection
                        )
                    )
                },
                error = stringRes("").takeIf { state.isError }
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
            ),
    )
}

@Immutable
data class SeedWordTextFieldState(
    val innerState: SeedWordInnerTextFieldState,
    val isError: Boolean,
    val onValueChange: (SeedWordInnerTextFieldState) -> Unit
)

@Immutable
data class SeedWordInnerTextFieldState(
    val value: String,
    val selection: TextSelection = TextSelection.Start,
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
                        innerState = SeedWordInnerTextFieldState(
                            value = "asd",
                            selection = TextSelection.Start
                        ),
                        isError = false,
                        onValueChange = {},
                    )
            )
        }
    }
