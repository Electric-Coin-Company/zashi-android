package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("LongParameterList")
@Composable
fun ZashiTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    textStyle: TextStyle = ZashiTypography.textMd.copy(fontWeight = FontWeight.Medium),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ZashiTextFieldDefaults.shape,
    colors: ZashiTextFieldColors = ZashiTextFieldDefaults.defaultColors()
) {
    TextFieldInternal(
        value = state.value.getValue(),
        onValueChange = state.onValueChange,
        modifier =
            modifier then
                Modifier.border(
                    width = 1.dp,
                    color = colors.borderColor,
                    shape = ZashiTextFieldDefaults.shape
                ),
        enabled = state.isEnabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = state.error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors.toTextFieldColors(),
    )
}

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextFieldInternal(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    readOnly: Boolean,
    textStyle: TextStyle,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    prefix: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    supportingText: @Composable (() -> Unit)?,
    isError: Boolean,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    singleLine: Boolean,
    maxLines: Int,
    minLines: Int,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    colors: TextFieldColors,
    modifier: Modifier = Modifier,
) {
    // If color is not provided via the text style, use content color as a default
    val textColor =
        textStyle.color.takeOrElse {
            colors.textColor(enabled, isError, interactionSource).value
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(
            value = value,
            modifier =
                modifier
                    .defaultMinSize(minWidth = TextFieldDefaults.MinWidth),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor(isError).value),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        )
    }
}

@Composable
private fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue =
        when {
            !enabled -> disabledTextColor
            isError -> errorTextColor
            focused -> focusedTextColor
            else -> unfocusedTextColor
        }
    return rememberUpdatedState(targetValue)
}

private val TextFieldColors.selectionColors: TextSelectionColors
    @Composable get() = textSelectionColors

@Composable
private fun TextFieldColors.cursorColor(isError: Boolean): State<Color> {
    return rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
}

@Immutable
data class ZashiTextFieldColors(
    val textColor: Color,
    val hintColor: Color,
    val borderColor: Color,
    val containerColor: Color,
)

@Composable
private fun ZashiTextFieldColors.toTextFieldColors() =
    TextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor,
        errorTextColor = Color.Unspecified,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        errorContainerColor = Color.Unspecified,
        cursorColor = Color.Unspecified,
        errorCursorColor = Color.Unspecified,
        selectionColors = null,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Unspecified,
        focusedLeadingIconColor = Color.Unspecified,
        unfocusedLeadingIconColor = Color.Unspecified,
        disabledLeadingIconColor = Color.Unspecified,
        errorLeadingIconColor = Color.Unspecified,
        focusedTrailingIconColor = Color.Unspecified,
        unfocusedTrailingIconColor = Color.Unspecified,
        disabledTrailingIconColor = Color.Unspecified,
        errorTrailingIconColor = Color.Unspecified,
        focusedLabelColor = Color.Unspecified,
        unfocusedLabelColor = Color.Unspecified,
        disabledLabelColor = Color.Unspecified,
        errorLabelColor = Color.Unspecified,
        focusedPlaceholderColor = hintColor,
        unfocusedPlaceholderColor = hintColor,
        disabledPlaceholderColor = hintColor,
        errorPlaceholderColor = Color.Unspecified,
        focusedSupportingTextColor = hintColor,
        unfocusedSupportingTextColor = hintColor,
        disabledSupportingTextColor = hintColor,
        errorSupportingTextColor = Color.Unspecified,
        focusedPrefixColor = Color.Unspecified,
        unfocusedPrefixColor = Color.Unspecified,
        disabledPrefixColor = Color.Unspecified,
        errorPrefixColor = Color.Unspecified,
        focusedSuffixColor = Color.Unspecified,
        unfocusedSuffixColor = Color.Unspecified,
        disabledSuffixColor = Color.Unspecified,
        errorSuffixColor = Color.Unspecified,
    )

object ZashiTextFieldDefaults {
    val shape: Shape
        get() = RoundedCornerShape(8.dp)

    @Composable
    fun defaultColors(
        textColor: Color = ZashiColors.Inputs.Default.text,
        hintColor: Color = ZashiColors.Inputs.Default.hint,
        borderColor: Color = ZashiColors.Inputs.Default.stroke,
        containerColor: Color = ZashiColors.Inputs.Default.bg
    ) = ZashiTextFieldColors(
        textColor = textColor,
        hintColor = hintColor,
        borderColor = borderColor,
        containerColor = containerColor
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ZashiTextFieldPreview() =
    ZcashTheme {
        ZashiTextField(
            state =
                TextFieldState(
                    value = stringRes("Text")
                ) {}
        )
    }
