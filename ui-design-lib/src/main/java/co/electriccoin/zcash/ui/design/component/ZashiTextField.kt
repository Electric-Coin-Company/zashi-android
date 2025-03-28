package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("LongParameterList")
@Composable
fun ZashiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = ZashiTextFieldDefaults.innerModifier,
    error: String? = null,
    isEnabled: Boolean = true,
    handle: ZashiTextFieldHandle =
        rememberZashiTextFieldHandle(
            TextFieldState(
                value = stringRes(value),
                error = error?.let { stringRes(it) },
                isEnabled = isEnabled,
                onValueChange = onValueChange,
            )
        ),
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
        innerModifier = innerModifier,
        handle = handle,
    )
}

@Suppress("LongParameterList")
@Composable
fun ZashiTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = ZashiTextFieldDefaults.innerModifier,
    handle: ZashiTextFieldHandle = rememberZashiTextFieldHandle(state),
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
    contentPadding: PaddingValues =
        PaddingValues(
            start = if (leadingIcon != null) 8.dp else 14.dp,
            end = if (suffix != null) 4.dp else 12.dp,
            top = getVerticalPadding(trailingIcon, leadingIcon, suffix, prefix),
            bottom = getVerticalPadding(trailingIcon, leadingIcon, suffix, prefix),
        ),
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
        contentPadding = contentPadding,
        innerModifier = innerModifier,
        handle = handle
    )
}

@Composable
fun ZashiTextFieldPlaceholder(res: StringResource) {
    Text(
        text = res.getValue(),
        style = ZashiTypography.textMd,
        color = ZashiColors.Inputs.Default.text
    )
}

@Stable
class ZashiTextFieldHandle(text: String) {
    var textFieldValueState by mutableStateOf(TextFieldValue(text = text))

    fun moveCursorToEnd() {
        textFieldValueState =
            textFieldValueState.copy(
                selection = TextRange(textFieldValueState.text.length),
            )
    }
}

@Composable
fun rememberZashiTextFieldHandle(state: TextFieldState): ZashiTextFieldHandle {
    val context = LocalContext.current
    return remember { ZashiTextFieldHandle(state.value.getString(context)) }
}

@Suppress("LongParameterList", "LongMethod")
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
    contentPadding: PaddingValues,
    handle: ZashiTextFieldHandle,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val value = remember(state.value) { state.value.getString(context) }
    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    val textFieldValueState = handle.textFieldValueState
    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = textFieldValueState.copy(text = value, selection = textFieldValueState.selection)

    SideEffect {
        if (textFieldValue.text != textFieldValueState.text ||
            textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            handle.textFieldValueState = textFieldValue
        }
    }

    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor by colors.borderColor(state, isFocused)
    val androidColors = colors.toTextFieldColors()
    // If color is not provided via the text style, use content color as a default
    val textColor =
        textStyle.color.takeOrElse {
            androidColors.textColor(state.isEnabled, state.isError, interactionSource).value
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    var lastTextValue by remember(value) { mutableStateOf(value) }

    CompositionLocalProvider(LocalTextSelectionColors provides androidColors.selectionColors) {
        Column(
            modifier = modifier,
        ) {
            BasicTextField(
                value = textFieldValue,
                modifier =
                    innerModifier then
                        if (borderColor == Color.Unspecified) {
                            Modifier
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = borderColor,
                                shape = shape
                            )
                        },
                onValueChange = { newTextFieldValueState ->
                    handle.textFieldValueState = newTextFieldValueState

                    val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
                    lastTextValue = newTextFieldValueState.text

                    if (stringChangedSinceLastInvocation) {
                        state.onValueChange(newTextFieldValueState.text)
                    }
                },
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
            ) { innerTextField: @Composable () -> Unit ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = state.value.getValue(),
                    visualTransformation = visualTransformation,
                    innerTextField = {
                        DecorationBox(prefix = prefix, suffix = suffix, content = innerTextField)
                    },
                    placeholder =
                        if (placeholder != null) {
                            {
                                DecorationBox(prefix, suffix, placeholder)
                            }
                        } else {
                            null
                        },
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
                    contentPadding = contentPadding
                )
            }

            if (state.error != null && state.error.getValue().isNotEmpty()) {
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

@ReadOnlyComposable
@Composable
private fun getVerticalPadding(
    trailingIcon: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    prefix: @Composable (() -> Unit)?
) = when {
    trailingIcon != null || leadingIcon != null -> 12.dp
    suffix != null || prefix != null -> 4.dp
    else -> 10.dp
}

@Composable
private fun DecorationBox(
    prefix: @Composable (() -> Unit)?,
    suffix: @Composable (() -> Unit)?,
    content: @Composable () -> Unit
) {
    Box(
        modifier =
            Modifier.padding(
                start = if (prefix != null) 4.dp else 0.dp,
                top = if (suffix != null || prefix != null) 8.dp else 0.dp,
                bottom = if (suffix != null || prefix != null) 8.dp else 0.dp,
                end = if (suffix != null) 4.dp else 0.dp
            )
    ) {
        content()
    }
}

@Immutable
data class ZashiTextFieldColors(
    val textColor: Color,
    val hintColor: Color,
    val borderColor: Color,
    val focusedBorderColor: Color,
    val containerColor: Color,
    val focusedContainerColor: Color,
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
    internal fun borderColor(
        state: TextFieldState,
        isFocused: Boolean
    ): State<Color> {
        val targetValue =
            when {
                !state.isEnabled -> disabledBorderColor
                state.isError -> errorBorderColor
                isFocused -> focusedBorderColor.takeOrElse { borderColor }
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
            focusedContainerColor = focusedContainerColor.takeOrElse { containerColor },
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

    val innerModifier: Modifier
        get() =
            Modifier
                .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
                .fillMaxWidth()

    @Suppress("LongParameterList")
    @Composable
    fun defaultColors(
        textColor: Color = ZashiColors.Inputs.Filled.text,
        hintColor: Color = ZashiColors.Inputs.Default.hint,
        borderColor: Color = Color.Unspecified,
        focusedBorderColor: Color = ZashiColors.Inputs.Focused.stroke,
        containerColor: Color = ZashiColors.Inputs.Default.bg,
        focusedContainerColor: Color = ZashiColors.Inputs.Focused.bg,
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
        focusedBorderColor = focusedBorderColor,
        containerColor = containerColor,
        focusedContainerColor = focusedContainerColor,
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

@Immutable
data class TextFieldState(
    val value: StringResource,
    val error: StringResource? = null,
    val isEnabled: Boolean = true,
    val onValueChange: (String) -> Unit,
) {
    val isError = error != null
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
