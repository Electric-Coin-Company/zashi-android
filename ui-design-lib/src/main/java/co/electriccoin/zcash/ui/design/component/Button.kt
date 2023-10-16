package co.electriccoin.zcash.ui.design.component

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ButtonComposablePreview() {
    ZcashTheme(darkTheme = false) {
        GradientSurface {
            Column {
                PrimaryButton(onClick = { }, text = "Primary")
                SecondaryButton(onClick = { }, text = "Secondary")
                TertiaryButton(onClick = { }, text = "Tertiary")
                NavigationButton(onClick = { }, text = "Navigation")
                DangerousButton(onClick = { }, text = "Dangerous")
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Button(
        shape = RectangleShape,
        enabled = enabled,
        modifier = modifier
            .padding(outerPaddingValues)
            .shadow(
                ZcashTheme.colors.buttonShadowColor,
                borderRadius = 0.dp,
                offsetX = ZcashTheme.dimens.shadowOffsetX,
                offsetY = ZcashTheme.dimens.shadowOffsetY,
                spread = ZcashTheme.dimens.shadowSpread,
                blurRadius = 0.dp,
                stroke = textColor != MaterialTheme.colorScheme.primary,
            )
            .translationClick(
                translationX = ZcashTheme.dimens.shadowOffsetX + 6.dp, // + 6dp to exactly cover the bottom shadow
                translationY = ZcashTheme.dimens.shadowOffsetX + 6.dp
            )
            .defaultMinSize(ZcashTheme.dimens.defaultButtonWidth, ZcashTheme.dimens.defaultButtonHeight)
            .border(1.dp, Color.Black),
        colors = buttonColors(
            buttonColor,
            disabledContainerColor = ZcashTheme.colors.disabledButtonColor,
            disabledContentColor = ZcashTheme.colors.disabledButtonTextColor
        ),
        onClick = onClick,
    ) {
        Text(
            style = ZcashTheme.extendedTypography.buttonText,
            textAlign = TextAlign.Center,
            text = text.uppercase(),
            color = textColor
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    Button(
        shape = RectangleShape,
        enabled = enabled,
        modifier = modifier
            .padding(outerPaddingValues)
            .shadow(
                ZcashTheme.colors.buttonShadowColor,
                borderRadius = 0.dp,
                offsetX = ZcashTheme.dimens.shadowOffsetX,
                offsetY = ZcashTheme.dimens.shadowOffsetY,
                spread = ZcashTheme.dimens.shadowSpread,
                blurRadius = 0.dp,
                stroke = textColor != MaterialTheme.colorScheme.primary,
            )
            .translationClick(
                translationX = ZcashTheme.dimens.shadowOffsetX + 6.dp, // + 6dp to exactly cover the bottom shadow
                translationY = ZcashTheme.dimens.shadowOffsetX + 6.dp
            )
            .defaultMinSize(ZcashTheme.dimens.defaultButtonWidth, ZcashTheme.dimens.defaultButtonHeight)
            .border(1.dp, Color.Black),
        colors = buttonColors(
            buttonColor,
            disabledContainerColor = ZcashTheme.colors.disabledButtonColor,
            disabledContentColor = ZcashTheme.colors.disabledButtonTextColor
        ),
        onClick = onClick,
    ) {
        Text(
            style = ZcashTheme.extendedTypography.buttonText,
            textAlign = TextAlign.Center,
            text = text.uppercase(),
            color = textColor
        )
    }
}

@Composable
fun NavigationButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
) {
    Button(
        shape = RectangleShape,
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .padding(outerPaddingValues)
        ),
        colors = buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            text = text,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun TertiaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
    enabled: Boolean = true
) {
    Button(
        shape = RectangleShape,
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(outerPaddingValues)
        ),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
        colors = buttonColors(containerColor = ZcashTheme.colors.tertiary)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            text = text,
            color = ZcashTheme.colors.onTertiary
        )
    }
}

@Composable
fun DangerousButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues = PaddingValues(
        horizontal = ZcashTheme.dimens.spacingDefault,
        vertical = ZcashTheme.dimens.spacingSmall
    ),
) {
    Button(
        shape = RectangleShape,
        onClick = onClick,
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(outerPaddingValues)
        ),
        colors = buttonColors(containerColor = ZcashTheme.colors.dangerous)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            text = text,
            color = ZcashTheme.colors.onDangerous
        )
    }
}

@Suppress("LongParameterList")
fun Modifier.shadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    stroke: Boolean = true,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            if (stroke) {
                paint.style = PaintingStyle.Stroke
                paint.strokeWidth = 2f
                paint.color = Color.Black
            }
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)

private enum class ButtonState { Pressed, Idle }
fun Modifier.translationClick(
    translationX: Dp = 0.dp,
    translationY: Dp = 0.dp
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val translationXAnimated by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) {
            translationX.value
        } else {
            0f
        },
        label = "ClickTranslationXAnimation",
        animationSpec = tween(
            durationMillis = 100
        )
    )
    val translationYAnimated by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) {
            translationY.value
        } else {
            0f
        },
        label = "ClickTranslationYAnimation",
        animationSpec = tween(
            durationMillis = 100
        )
    )

    this
        .graphicsLayer {
            this.translationX = translationXAnimated
            this.translationY = translationYAnimated
        }
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}
