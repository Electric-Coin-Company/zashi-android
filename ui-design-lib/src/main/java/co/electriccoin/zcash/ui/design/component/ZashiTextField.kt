package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    error: String? = null,
    isEnabled: Boolean = true,
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
    ZashiTextField(
        state =
            TextFieldState(
                value = stringRes(value),
                error = error?.let { stringRes(it) },
                isEnabled = isEnabled,
                onValueChange = onValueChange,
            ),
        modifier = modifier,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        innerModifier = innerModifier
    )
}

@Suppress("LongParameterList")
@Composable
fun ZashiTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
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
        state = state,
        modifier = modifier,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        innerModifier = innerModifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextFieldInternal(
    state: TextFieldState,
    readOnly: Boolean,
    textStyle: TextStyle,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    prefix: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    supportingText: @Composable (() -> Unit)?,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    singleLine: Boolean,
    maxLines: Int,
    minLines: Int,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    colors: ZashiTextFieldColors,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
) {
    val borderColor by colors.borderColor(state)
    val androidColors = colors.toTextFieldColors()
    // If color is not provided via the text style, use content color as a default
    val textColor =
        textStyle.color.takeOrElse {
            androidColors.textColor(state.isEnabled, state.isError, interactionSource).value
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides androidColors.selectionColors) {
        Column(
            modifier = modifier,
        ) {
            BasicTextField(
                value = state.value.getValue(),
                modifier =
                    innerModifier.fillMaxWidth() then
                        if (borderColor == Color.Unspecified) {
                            Modifier
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = borderColor,
                                shape = ZashiTextFieldDefaults.shape
                            )
                        } then Modifier.defaultMinSize(minWidth = TextFieldDefaults.MinWidth),
                onValueChange = state.onValueChange,
                enabled = state.isEnabled,
                readOnly = readOnly,
                textStyle = mergedTextStyle,
                cursorBrush = SolidColor(androidColors.cursorColor(state.isError).value),
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
                        value = state.value.getValue(),
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
                        enabled = state.isEnabled,
                        isError = state.isError,
                        interactionSource = interactionSource,
                        colors = androidColors,
                        contentPadding =
                            PaddingValues(
                                horizontal = 12.dp,
                                vertical = if (trailingIcon != null || leadingIcon != null) 12.dp else 8.dp,
                            )
                    )
                }
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.error.getValue(),
                    style = ZashiTypography.textSm,
                    color = colors.hintColor(state).value
                )
            }
        }
    }
}

@Immutable
data class ZashiTextFieldColors(
    val textColor: Color,
    val hintColor: Color,
    val borderColor: Color,
    val containerColor: Color,
    val placeholderColor: Color,
    val disabledTextColor: Color,
    val disabledHintColor: Color,
    val disabledBorderColor: Color,
    val disabledContainerColor: Color,
    val disabledPlaceholderColor: Color,
    val errorTextColor: Color,
    val errorHintColor: Color,
    val errorBorderColor: Color,
    val errorContainerColor: Color,
    val errorPlaceholderColor: Color,
) {
    @Composable
    internal fun borderColor(state: TextFieldState): State<Color> {
        val targetValue =
            when {
                !state.isEnabled -> disabledBorderColor
                state.isError -> errorBorderColor
                else -> borderColor
            }
        return rememberUpdatedState(targetValue)
    }

    @Composable
    internal fun hintColor(state: TextFieldState): State<Color> {
        val targetValue =
            when {
                !state.isEnabled -> disabledHintColor
                state.isError -> errorHintColor
                else -> hintColor
            }
        return rememberUpdatedState(targetValue)
    }

    @Composable
    internal fun toTextFieldColors() =
        TextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified,
            selectionColors = null,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
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
            focusedPlaceholderColor = placeholderColor,
            unfocusedPlaceholderColor = placeholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedSupportingTextColor = hintColor,
            unfocusedSupportingTextColor = hintColor,
            disabledSupportingTextColor = disabledHintColor,
            errorSupportingTextColor = errorHintColor,
            focusedPrefixColor = Color.Unspecified,
            unfocusedPrefixColor = Color.Unspecified,
            disabledPrefixColor = Color.Unspecified,
            errorPrefixColor = Color.Unspecified,
            focusedSuffixColor = Color.Unspecified,
            unfocusedSuffixColor = Color.Unspecified,
            disabledSuffixColor = Color.Unspecified,
            errorSuffixColor = Color.Unspecified,
        )
}

object ZashiTextFieldDefaults {
    val shape: Shape
        get() = RoundedCornerShape(8.dp)

    @Composable
    fun defaultColors(
        textColor: Color = ZashiColors.Inputs.Filled.text,
        hintColor: Color = ZashiColors.Inputs.Default.hint,
        borderColor: Color = Color.Unspecified,
        containerColor: Color = ZashiColors.Inputs.Default.bg,
        placeholderColor: Color = ZashiColors.Inputs.Default.text,
        disabledTextColor: Color = ZashiColors.Inputs.Disabled.text,
        disabledHintColor: Color = ZashiColors.Inputs.Disabled.hint,
        disabledBorderColor: Color = ZashiColors.Inputs.Disabled.stroke,
        disabledContainerColor: Color = ZashiColors.Inputs.Disabled.bg,
        disabledPlaceholderColor: Color = ZashiColors.Inputs.Disabled.text,
        errorTextColor: Color = ZashiColors.Inputs.ErrorFilled.text,
        errorHintColor: Color = ZashiColors.Inputs.ErrorDefault.hint,
        errorBorderColor: Color = ZashiColors.Inputs.ErrorDefault.stroke,
        errorContainerColor: Color = ZashiColors.Inputs.ErrorDefault.bg,
        errorPlaceholderColor: Color = ZashiColors.Inputs.ErrorDefault.text,
    ) = ZashiTextFieldColors(
        textColor = textColor,
        hintColor = hintColor,
        borderColor = borderColor,
        containerColor = containerColor,
        placeholderColor = placeholderColor,
        disabledTextColor = disabledTextColor,
        disabledHintColor = disabledHintColor,
        disabledBorderColor = disabledBorderColor,
        disabledContainerColor = disabledContainerColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        errorTextColor = errorTextColor,
        errorHintColor = errorHintColor,
        errorBorderColor = errorBorderColor,
        errorContainerColor = errorContainerColor,
        errorPlaceholderColor = errorPlaceholderColor,
    )
}

@PreviewScreens
@Composable
private fun DefaultPreview() =
    ZcashTheme {
        ZashiTextField(
            state =
                TextFieldState(
                    value = stringRes("Text")
                ) {}
        )
    }

@PreviewScreens
@Composable
private fun ErrorPreview() =
    ZcashTheme {
        ZashiTextField(
            state =
                TextFieldState(
                    value = stringRes("Text"),
                    error = stringRes("Error"),
                ) {}
        )
    }
