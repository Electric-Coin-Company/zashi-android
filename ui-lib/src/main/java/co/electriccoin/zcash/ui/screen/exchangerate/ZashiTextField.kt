package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("LongParameterList")
@Composable
fun ZashiTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
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
    colors: TextFieldColors = ZashiTextFieldDefaults.colors()
) {
    TextFieldInternal(
        value = state.value.getValue(),
        onValueChange = state.onValueChange,
        modifier = modifier then Modifier.border(
            width = 1.dp,
            color = ZcashTheme.zashiColors.stroke,
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
        colors = colors,
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
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.selectionColors) {
        BasicTextField(
            value = value,
            modifier = modifier
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

    val targetValue = when {
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
internal fun TextFieldColors.cursorColor(isError: Boolean): State<Color> {
    return rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
}

object ZashiTextFieldDefaults {

    val shape: Shape
        get() = RoundedCornerShape(8.dp)

    @Suppress("LongParameterList")
    @Composable
    fun colors(
        focusedTextColor: Color = ZcashTheme.zashiColors.textPrimary,
        unfocusedTextColor: Color = ZcashTheme.zashiColors.textPrimary,
        disabledTextColor: Color = ZcashTheme.zashiColors.textPrimary,
        errorTextColor: Color = Color.Unspecified,
        focusedContainerColor: Color = if (isSystemInDarkTheme()) {
            ZcashTheme.zashiColors.bgSecondary
        } else {
            ZcashTheme.zashiColors.bgPrimary
        },
        unfocusedContainerColor: Color = if (isSystemInDarkTheme()) {
            ZcashTheme.zashiColors.bgSecondary
        } else {
            ZcashTheme.zashiColors.bgPrimary
        },
        disabledContainerColor: Color = if (isSystemInDarkTheme()) {
            ZcashTheme.zashiColors.bgSecondary
        } else {
            ZcashTheme.zashiColors.bgPrimary
        },
        errorContainerColor: Color = Color.Unspecified,
        cursorColor: Color = Color.Unspecified,
        errorCursorColor: Color = Color.Unspecified,
        selectionColors: TextSelectionColors? = null,
        focusedIndicatorColor: Color = Color.Transparent,
        unfocusedIndicatorColor: Color = Color.Transparent,
        disabledIndicatorColor: Color = Color.Transparent,
        errorIndicatorColor: Color = Color.Unspecified,
        focusedLeadingIconColor: Color = Color.Unspecified,
        unfocusedLeadingIconColor: Color = Color.Unspecified,
        disabledLeadingIconColor: Color = Color.Unspecified,
        errorLeadingIconColor: Color = Color.Unspecified,
        focusedTrailingIconColor: Color = Color.Unspecified,
        unfocusedTrailingIconColor: Color = Color.Unspecified,
        disabledTrailingIconColor: Color = Color.Unspecified,
        errorTrailingIconColor: Color = Color.Unspecified,
        focusedLabelColor: Color = Color.Unspecified,
        unfocusedLabelColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        errorLabelColor: Color = Color.Unspecified,
        focusedPlaceholderColor: Color = ZcashTheme.zashiColors.textSupport,
        unfocusedPlaceholderColor: Color = ZcashTheme.zashiColors.textSupport,
        disabledPlaceholderColor: Color = ZcashTheme.zashiColors.textSupport,
        errorPlaceholderColor: Color = Color.Unspecified,
        focusedSupportingTextColor: Color = ZcashTheme.zashiColors.textSupport,
        unfocusedSupportingTextColor: Color = ZcashTheme.zashiColors.textSupport,
        disabledSupportingTextColor: Color = ZcashTheme.zashiColors.textSupport,
        errorSupportingTextColor: Color = Color.Unspecified,
        focusedPrefixColor: Color = Color.Unspecified,
        unfocusedPrefixColor: Color = Color.Unspecified,
        disabledPrefixColor: Color = Color.Unspecified,
        errorPrefixColor: Color = Color.Unspecified,
        focusedSuffixColor: Color = Color.Unspecified,
        unfocusedSuffixColor: Color = Color.Unspecified,
        disabledSuffixColor: Color = Color.Unspecified,
        errorSuffixColor: Color = Color.Unspecified,
    ): TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = focusedTextColor,
        unfocusedTextColor = unfocusedTextColor,
        disabledTextColor = disabledTextColor,
        errorTextColor = errorTextColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor = errorContainerColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        selectionColors = selectionColors,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        focusedLeadingIconColor = focusedLeadingIconColor,
        unfocusedLeadingIconColor = unfocusedLeadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = focusedTrailingIconColor,
        unfocusedTrailingIconColor = unfocusedTrailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        focusedPlaceholderColor = focusedPlaceholderColor,
        unfocusedPlaceholderColor = unfocusedPlaceholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        errorPlaceholderColor = errorPlaceholderColor,
        focusedSupportingTextColor = focusedSupportingTextColor,
        unfocusedSupportingTextColor = unfocusedSupportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor,
        focusedPrefixColor = focusedPrefixColor,
        unfocusedPrefixColor = unfocusedPrefixColor,
        disabledPrefixColor = disabledPrefixColor,
        errorPrefixColor = errorPrefixColor,
        focusedSuffixColor = focusedSuffixColor,
        unfocusedSuffixColor = unfocusedSuffixColor,
        disabledSuffixColor = disabledSuffixColor,
        errorSuffixColor = errorSuffixColor,
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ZashiTextFieldPreview() = ZcashTheme {
    ZashiTextField(
        state = TextFieldState(
            value = stringRes("Text")
        ) {}
    )
}
