@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.restore.height

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldPlaceholder
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import java.text.DecimalFormat
import java.text.NumberFormat

@Composable
fun RestoreBDHeightView(state: RestoreBDHeightState) {
    BlankBgScaffold(
        topBar = { AppBar(state) },
        bottomBar = {},
        content = { padding ->
            Content(
                state = state,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .scaffoldPadding(padding)
            )
        }
    )
}

@Composable
private fun Content(
    state: RestoreBDHeightState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.restore_bd_subtitle),
            style = ZashiTypography.header6,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.restore_bd_message),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.restore_bd_text_field_title),
            style = ZashiTypography.textSm,
            color = ZashiColors.Inputs.Default.label,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        ZashiTextField(
            state = state.blockHeight,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                ZashiTextFieldPlaceholder(
                    stringRes(R.string.restore_bd_text_field_hint)
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
            visualTransformation = ThousandSeparatorTransformation()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.restore_bd_text_field_note),
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textTertiary
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(24.dp))

        ZashiButton(
            state.estimate,
            modifier = Modifier.fillMaxWidth(),
            colors = ZashiButtonDefaults.secondaryColors()
        )

        Spacer(Modifier.height(12.dp))

        ZashiButton(
            state.restore,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(RestoreBDHeightTags.RESTORE_BTN),
        )
    }
}

private class ThousandSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val symbols = DecimalFormat().decimalFormatSymbols
        val decimalSeparator = symbols.decimalSeparator

        var outputText = ""
        val integerPart: Long
        val decimalPart: String

        if (text.text.isNotEmpty()) {
            val number = text.text.toDouble()
            integerPart = number.toLong()
            outputText += NumberFormat.getIntegerInstance().format(integerPart)
            if (text.text.contains(decimalSeparator)) {
                decimalPart = text.text.substring(text.text.indexOf(decimalSeparator))
                if (decimalPart.isNotEmpty()) {
                    outputText += decimalPart
                }
            }
        }

        val numberOffsetTranslator =
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return outputText.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.length
                }
            }

        return TransformedText(
            text = AnnotatedString(outputText),
            offsetMapping = numberOffsetTranslator
        )
    }
}

@Composable
private fun AppBar(state: RestoreBDHeightState) {
    ZashiSmallTopAppBar(
        title = stringResource(R.string.restore_title),
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = state.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        regularActions = {
            ZashiIconButton(state.dialogButton, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(20.dp))
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        RestoreBDHeightView(
            state =
                RestoreBDHeightState(
                    onBack = {},
                    dialogButton = IconButtonState(R.drawable.ic_restore_dialog) {},
                    blockHeight = TextFieldState(stringRes("")) {},
                    estimate = ButtonState(stringRes("Estimate")) {},
                    restore = ButtonState(stringRes("Restore")) {}
                )
        )
    }
