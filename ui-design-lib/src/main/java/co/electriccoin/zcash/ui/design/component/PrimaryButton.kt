package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.ButtonColors

@Composable
@Suppress("LongParameterList", "LongMethod")
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    contentScope: @Composable (PrimaryButtonContentScope) -> Unit = { buttonScope ->
        buttonScope.PrimaryButtonContent(
            modifier = Modifier.fillMaxWidth(),
            shouldTextFillMaxSize = true
        )
    },
    minWidth: Dp = ZcashTheme.dimens.buttonWidth,
    minHeight: Dp = ZcashTheme.dimens.buttonHeight,
    enabled: Boolean = true,
    showProgressBar: Boolean = false,
    buttonColors: ButtonColors = ZcashTheme.colors.primaryButtonColors,
    textStyle: TextStyle = ZcashTheme.extendedTypography.buttonText,
    outerPaddingValues: PaddingValues =
        PaddingValues(
            horizontal = ZcashTheme.dimens.spacingNone,
            vertical = ZcashTheme.dimens.spacingSmall
        ),
    contentPaddingValues: PaddingValues = PaddingValues(all = 17.dp)
) {
    val primaryButtonContentScope = remember(textStyle, text, enabled, buttonColors, showProgressBar) {
        object : PrimaryButtonContentScope {
            @Composable
            override fun PrimaryButtonContent(modifier: Modifier, shouldTextFillMaxSize: Boolean) {
                Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        text = text.uppercase(),
                        color =
                        if (enabled) {
                            buttonColors.textColor
                        } else {
                            buttonColors.disabledTextColor
                        },
                        modifier = Modifier.then(
                            if (shouldTextFillMaxSize) {
                                Modifier.weight(1f)
                            } else {
                                Modifier
                            }
                        )
                    )

                    if (showProgressBar) {
                        Spacer(modifier = Modifier.width(12.dp))
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    Button(
        shape = RectangleShape,
        enabled = enabled,
        contentPadding = contentPaddingValues,
        modifier =
        modifier.then(
            Modifier
                .padding(outerPaddingValues)
                .shadow(
                    contentColor =
                    if (enabled) {
                        buttonColors.shadowColor
                    } else {
                        buttonColors.disabledShadowColor
                    },
                    strokeColor =
                    if (enabled) {
                        buttonColors.shadowStrokeColor
                    } else {
                        buttonColors.shadowDisabledStrokeColor
                    },
                    strokeWidth = 1.dp,
                    offsetX = ZcashTheme.dimens.buttonShadowOffsetX,
                    offsetY = ZcashTheme.dimens.buttonShadowOffsetY,
                    spread = ZcashTheme.dimens.buttonShadowSpread,
                )
                .then(
                    if (enabled) {
                        Modifier.translationClick(
                            // + 6dp to exactly cover the bottom shadow
                            translationX = ZcashTheme.dimens.buttonShadowOffsetX + 6.dp,
                            translationY = ZcashTheme.dimens.buttonShadowOffsetX + 6.dp
                        )
                    } else {
                        Modifier
                    }
                )
                .defaultMinSize(minWidth, minHeight)
                .border(
                    width = 1.dp,
                    color =
                    if (enabled) {
                        buttonColors.strokeColor
                    } else {
                        buttonColors.disabledStrokeColor
                    }
                )
        ),
        colors =
        ButtonDefaults.buttonColors(
            containerColor = buttonColors.containerColor,
            disabledContainerColor = buttonColors.disabledContainerColor,
            disabledContentColor = buttonColors.disabledContainerColor,
        ),
        onClick = onClick,
    ) {
        contentScope(primaryButtonContentScope)
    }
}

interface PrimaryButtonContentScope {

    @Composable
    fun PrimaryButtonContent(modifier: Modifier, shouldTextFillMaxSize: Boolean)
}
