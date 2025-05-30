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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.FRACTION_DIGITS
import cash.z.ecc.android.sdk.model.MonetarySeparators
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import java.math.BigDecimal
import java.text.ParseException
import java.util.Locale

@Suppress("LongParameterList")
@Composable
fun ZashiNumberTextField(
    state: NumberTextFieldState,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = ZashiTextFieldDefaults.innerModifier,
    textStyle: TextStyle = ZashiTypography.textMd.copy(fontWeight = FontWeight.Medium),
    placeholder: @Composable (() -> Unit)? = { Text("0") },
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ZashiTextFieldDefaults.shape,
    contentPadding: PaddingValues =
        ZashiTextFieldDefaults.contentPadding(
            leadingIcon = null,
            suffix = suffix,
            trailingIcon = null,
            prefix = prefix
        ),
    colors: ZashiTextFieldColors = ZashiTextFieldDefaults.defaultColors()
) {
    val locale = LocalConfiguration.current.locales[0]
    val monetarySeparators by remember { derivedStateOf { MonetarySeparators.current(locale) } }
    val defaultRegex by remember { derivedStateOf { defaultRegex(monetarySeparators) } }
    val allowedNumbersRegex by remember { derivedStateOf { allowedNumbersRegex(monetarySeparators) } }
    val textFieldState =
        TextFieldState(
            value = state.text,
            error = state.errorString.takeIf { state.amount == null && state.text.getValue().isNotEmpty() },
            onValueChange = { text ->
                val amount =
                    parseAmount(
                        defaultAmountValidationRegex = defaultRegex,
                        allowedNumberFormatValidationRegex = allowedNumbersRegex,
                        text = text,
                        locale = locale
                    )
                state.onValueChange(state.copy(text = stringRes(text), amount = amount))
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

/**
 * Validates only numbers the properly use grouping and decimal separators
 * Note that this regex aligns with the one from ZcashSDK (sdk-incubator-lib/src/main/res/values/strings-regex.xml)
 * It only adds check for 0-8 digits after the decimal separator at maximum
 */
@Suppress("MaxLineLength")
private fun allowedNumbersRegex(monetarySeparators: MonetarySeparators) =
    "^([0-9]*([0-9]+([${monetarySeparators.grouping}]\$|[${monetarySeparators.grouping}][0-9]+))*([${monetarySeparators.decimal}]\$|[${monetarySeparators.decimal}][0-9]{0,8})?)?\$"
        .toRegex()

/**
 * Validates only zeros and decimal separator
 */
private fun defaultRegex(monetarySeparators: MonetarySeparators) = "^[0${monetarySeparators.decimal}]*$".toRegex()

private fun parseAmount(
    defaultAmountValidationRegex: Regex,
    allowedNumberFormatValidationRegex: Regex,
    text: String,
    locale: Locale
): BigDecimal? {
    if (text.contains(defaultAmountValidationRegex) || text.contains(allowedNumberFormatValidationRegex)) {
        val decimalFormat =
            android.icu.text.NumberFormat.getInstance(locale, android.icu.text.NumberFormat.NUMBERSTYLE).apply {
                // TODO [#343]: https://github.com/zcash/secant-android-wallet/issues/343
                roundingMode = android.icu.math.BigDecimal.ROUND_UNNECESSARY // aka Bankers rounding
                maximumFractionDigits = FRACTION_DIGITS
                minimumFractionDigits = FRACTION_DIGITS
            }

        return try {
            when (val result = decimalFormat.parse(text)) {
                is Int -> result.toBigDecimal()
                is Float -> result.toBigDecimal()
                is Double -> result.toBigDecimal()
                is Short -> result.toFloat().toBigDecimal()
                is BigDecimal -> result
                else -> result.toDouble().toBigDecimal()
            }
        } catch (e: ParseException) {
            null
        }
    } else {
        return null
    }
}

@Immutable
data class NumberTextFieldState(
    val text: StringResource = stringRes(""),
    val amount: BigDecimal? = null,
    val errorString: StringResource = stringRes(""),
    val onValueChange: (NumberTextFieldState) -> Unit,
)

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
