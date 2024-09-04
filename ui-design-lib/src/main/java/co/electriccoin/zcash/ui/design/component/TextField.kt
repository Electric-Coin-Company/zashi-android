package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongParameterList", "LongMethod")
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = ZcashTheme.extendedTypography.textFieldValue,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    colors: TextFieldColors =
        TextFieldDefaults.colors(
            cursorColor = ZcashTheme.colors.textPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = TextFieldDefaults.shape,
    // To enable border around the TextField
    withBorder: Boolean = true,
    bringIntoViewRequester: BringIntoViewRequester? = null,
    minHeight: Dp = ZcashTheme.dimens.textFieldDefaultHeight,
    testTag: String? = null
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.then(modifier)) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder =
                if (enabled) {
                    placeholder
                } else {
                    null
                },
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            colors = colors,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minHeight)
                    .onFocusEvent { focusState ->
                        bringIntoViewRequester?.run {
                            if (focusState.isFocused) {
                                coroutineScope.launch {
                                    bringIntoView()
                                }
                            }
                        }
                    }
                    .then(
                        if (withBorder) {
                            Modifier.border(
                                width = 1.dp,
                                color =
                                    if (enabled) {
                                        ZcashTheme.colors.textFieldFrame
                                    } else {
                                        ZcashTheme.colors.textDisabled
                                    }
                            )
                        } else {
                            Modifier
                        }
                    )
                    .then(
                        if (testTag.isNullOrEmpty()) {
                            Modifier
                        } else {
                            Modifier.testTag(testTag)
                        }
                    ),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardActions = keyboardActions,
            shape = shape,
            enabled = enabled
        )

        if (!error.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

            BodySmall(
                text = error,
                color = ZcashTheme.colors.textFieldWarning,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Suppress("LongParameterList")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FormTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = ZcashTheme.extendedTypography.textFieldValue,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    colors: TextFieldColors =
        TextFieldDefaults.colors(
            cursorColor = ZcashTheme.colors.textPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = TextFieldDefaults.shape,
    withBorder: Boolean = true,
    bringIntoViewRequester: BringIntoViewRequester? = null,
    minHeight: Dp = ZcashTheme.dimens.textFieldDefaultHeight,
    testTag: String? = null
) {
    FormTextField(
        value = state.value.getValue(),
        error = state.error?.getValue(),
        enabled = state.isEnabled,
        onValueChange = state.onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        colors = colors,
        keyboardActions = keyboardActions,
        shape = shape,
        withBorder = withBorder,
        bringIntoViewRequester = bringIntoViewRequester,
        minHeight = minHeight,
        testTag = testTag,
    )
}

data class TextFieldState(
    val value: StringResource,
    val isEnabled: Boolean = true,
    val error: StringResource? = null,
    val onValueChange: (String) -> Unit,
)
