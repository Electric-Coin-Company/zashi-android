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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.UserInputNumberParser
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
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
    leadingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ZashiTextFieldDefaults.shape,
    contentPadding: PaddingValues =
        ZashiTextFieldDefaults.contentPadding(
            leadingIcon = leadingIcon,
            suffix = suffix,
            trailingIcon = null,
            prefix = prefix
        ),
    colors: ZashiTextFieldColors = ZashiTextFieldDefaults.defaultColors()
) {
    val textFieldState = createTextFieldState(state)
    ZashiTextField(
        state = textFieldState,
        modifier = modifier,
        innerModifier = innerModifier,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
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

@Composable
private fun createTextFieldState(state: NumberTextFieldState): EnhancedTextFieldState {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]

    val text =
        state.innerState.innerTextFieldState.value
            .getValue()
            .replace(" ", "")
    val selection =
        when (val selection = state.innerState.innerTextFieldState.selection) {
            is TextSelection.ByTextRange ->
                TextSelection.ByTextRange(
                    TextRange(selection.range.start, selection.range.end.coerceAtMost(text.length))
                )

            TextSelection.End -> selection
            TextSelection.Start -> selection
        }

    val textFieldState =
        EnhancedTextFieldState(
            innerState =
                state.innerState.innerTextFieldState.copy(
                    value = stringRes(text),
                    selection = selection
                ),
            isEnabled = state.isEnabled,
            error = state.errorString.takeIf { state.innerState.isError },
            onValueChange = { innerState ->
                val newText = innerState.value.getString(context, locale)
                val normalized: String
                val amount: BigDecimal?
                val lastValidAmount: BigDecimal?

                if (newText != text) {
                    normalized =
                        UserInputNumberParser.normalizeInput(
                            input = innerState.value.getString(context, locale),
                            locale = locale
                        )
                    amount = UserInputNumberParser.toBigDecimalOrNull(normalized, locale)
                    lastValidAmount = amount ?: state.innerState.lastValidAmount
                } else {
                    normalized = text
                    amount = state.innerState.amount
                    lastValidAmount = state.innerState.lastValidAmount
                }

                val new =
                    state.innerState.copy(
                        innerTextFieldState =
                            state.innerState.innerTextFieldState.copy(
                                value = stringRes(normalized),
                                selection = innerState.selection
                            ),
                        amount = amount,
                        lastValidAmount = lastValidAmount
                    )
                state.onValueChange(new)
            }
        )
    return textFieldState
}

@Immutable
data class NumberTextFieldState(
    val innerState: NumberTextFieldInnerState = NumberTextFieldInnerState(),
    val isEnabled: Boolean = true,
    val errorString: StringResource = stringRes(""),
    val onValueChange: (NumberTextFieldInnerState) -> Unit,
)

@Immutable
data class NumberTextFieldInnerState(
    val innerTextFieldState: InnerTextFieldState =
        InnerTextFieldState(
            value = stringRes(value = ""),
            selection = TextSelection.Start
        ),
    val amount: BigDecimal? = null,
    val lastValidAmount: BigDecimal? = null,
) {
    val isError = amount == null && !innerTextFieldState.value.isEmpty()

    companion object {
        fun fromAmount(amount: BigDecimal) =
            NumberTextFieldInnerState(
                innerTextFieldState =
                    InnerTextFieldState(
                        value = stringResByNumber(amount),
                        selection = TextSelection.Start
                    ),
                amount = amount,
                lastValidAmount = amount
            )
    }
}

object ZashiNumberTextFieldDefaults {
    val textStyle
        @Composable get() = ZashiTypography.textMd.copy(fontWeight = FontWeight.Medium)

    @Composable
    fun Placeholder(
        modifier: Modifier = Modifier,
        style: TextStyle = ZashiTypography.textMd,
        fontWeight: FontWeight = FontWeight.Normal,
        textAlign: TextAlign = TextAlign.Start
    ) {
        Text(
            text = stringResByDynamicNumber(0).getValue(),
            modifier = modifier,
            style = style,
            fontWeight = fontWeight,
            textAlign = textAlign,
        )
    }
}

@Composable
@Preview
private fun Preview() =
    ZcashTheme {
        var innerState by remember { mutableStateOf(NumberTextFieldInnerState()) }
        val state by remember {
            derivedStateOf {
                NumberTextFieldState(
                    innerState = innerState,
                    onValueChange = { innerState = it }
                )
            }
        }

        BlankSurface {
            Column(modifier = Modifier.fillMaxSize()) {
                ZashiNumberTextField(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(36.dp)

                Text(
                    text = "Result: $state"
                )
            }
        }
    }
