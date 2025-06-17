package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.UserInputNumberParser
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.stringRes
import java.math.BigDecimal

@Suppress("LongParameterList")
@Composable
fun ZashiNumberTextField(
    state: NumberTextFieldState,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = ZashiTextFieldDefaults.innerModifier,
    textStyle: TextStyle = ZashiNumberTextFieldDefaults.textStyle,
    placeholder: @Composable (() -> Unit)? = { ZashiNumberTextFieldDefaults.Placeholder() },
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ZashiTextFieldDefaults.shape,
    contentPadding: PaddingValues = ZashiNumberTextFieldDefaults.contentPadding(suffix, prefix),
    colors: ZashiTextFieldColors = ZashiTextFieldDefaults.defaultColors()
) {
    val locale = LocalConfiguration.current.locales[0]
    val textFieldState =
        TextFieldState(
            value = state.text,
            error = state.errorString.takeIf { state.isError },
            onValueChange = { text ->
                val normalized = UserInputNumberParser.normalizeInput(text, locale)
                val amount = UserInputNumberParser.toBigDecimalOrNull(normalized, locale)
                state.onValueChange(state.copy(text = stringRes(normalized), amount = amount))
            }
        )
    val handle: ZashiTextFieldHandle = rememberZashiTextFieldHandle(textFieldState)
    ZashiTextField(
        state = textFieldState,
        modifier = modifier,
        innerModifier = innerModifier,
        handle = handle,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = null,
        trailingIcon = null,
        prefix = prefix,
        suffix = suffix,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
    )
}

@Immutable
data class NumberTextFieldState(
    val text: StringResource = stringRes(""),
    val amount: BigDecimal? = null,
    val errorString: StringResource = stringRes(""),
    val onValueChange: (NumberTextFieldState) -> Unit,
) {
    val isError = amount == null && !text.isEmpty()
}

object ZashiNumberTextFieldDefaults {
    val textStyle
        @Composable get() = ZashiTypography.textMd.copy(fontWeight = FontWeight.Medium)

    @Composable
    fun contentPadding(
        suffix: @Composable (() -> Unit)?,
        prefix: @Composable (() -> Unit)?
    ) = ZashiTextFieldDefaults.contentPadding(
        leadingIcon = null,
        suffix = suffix,
        trailingIcon = null,
        prefix = prefix
    )

    @Composable
    fun Placeholder() {
        Text("0")
    }
}

@Composable
@Preview
private fun Preview() =
    ZcashTheme {
        var state by remember { mutableStateOf(NumberTextFieldState(onValueChange = { })) }

        BlankSurface {
            Column(modifier = Modifier.fillMaxSize()) {
                ZashiNumberTextField(
                    state =
                        state.copy(
                            onValueChange = { state = it }
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(36.dp)

                Text(
                    text = "Result: $state"
                )
            }
        }
    }
