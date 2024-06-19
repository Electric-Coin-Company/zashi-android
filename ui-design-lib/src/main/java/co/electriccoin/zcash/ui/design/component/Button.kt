package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.ButtonColors

@Preview
@Composable
private fun ButtonComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Column(Modifier.padding(ZcashTheme.dimens.spacingDefault)) {
                Column(
                    modifier =
                        Modifier
                            .background(color = Color.Gray)
                            .padding(all = 24.dp)
                ) {
                    PrimaryButton(onClick = { }, text = "Primary Basic")
                    PrimaryButton(onClick = { }, text = "Primary Disabled", enabled = false)
                    SecondaryButton(onClick = { }, text = "Secondary Basic")
                    SecondaryButton(onClick = { }, text = "Secondary Disabled", enabled = false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(onClick = { }, text = "Primary loading", showProgressBar = true)

                Spacer(modifier = Modifier.height(24.dp))

                @Suppress("MagicNumber")
                Row {
                    PrimaryButton(onClick = { }, text = "Button 1", modifier = Modifier.weight(0.5f))
                    Spacer(modifier = Modifier.width(24.dp))
                    PrimaryButton(onClick = { }, text = "Button 2", modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ButtonComposableDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        BlankSurface {
            Column(Modifier.padding(ZcashTheme.dimens.spacingDefault)) {
                Column(
                    modifier =
                        Modifier
                            .background(color = Color.Gray)
                            .padding(all = 24.dp)
                ) {
                    PrimaryButton(onClick = { }, text = "Primary Basic")
                    PrimaryButton(onClick = { }, text = "Primary Disabled", enabled = false)
                    SecondaryButton(onClick = { }, text = "Secondary Basic")
                    SecondaryButton(onClick = { }, text = "Secondary Disabled", enabled = false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(onClick = { }, text = "Primary loading", showProgressBar = true)

                Spacer(modifier = Modifier.height(24.dp))

                @Suppress("MagicNumber")
                Row {
                    PrimaryButton(onClick = { }, text = "Button 1", modifier = Modifier.weight(0.5f))
                    Spacer(modifier = Modifier.width(24.dp))
                    PrimaryButton(onClick = { }, text = "Button 2", modifier = Modifier.weight(0.5f))
                }
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
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
    contentPaddingValues: PaddingValues = PaddingValues(all = 16.5.dp)
) {
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
            buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                disabledContentColor = buttonColors.disabledContainerColor,
            ),
        onClick = onClick,
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (title, spacer, progress) = createRefs()

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
                modifier =
                    Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            if (showProgressBar) {
                Spacer(
                    modifier =
                        Modifier
                            .width(12.dp)
                            .constrainAs(spacer) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(title.end)
                                end.linkTo(progress.start)
                            }
                )

                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier =
                        Modifier
                            .size(18.dp)
                            .constrainAs(progress) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(spacer.end)
                            }
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList", "LongMethod")
fun SecondaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    minWidth: Dp = ZcashTheme.dimens.buttonWidth,
    minHeight: Dp = ZcashTheme.dimens.buttonHeight,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ZcashTheme.colors.secondaryButtonColors,
    outerPaddingValues: PaddingValues =
        PaddingValues(
            horizontal = ZcashTheme.dimens.spacingNone,
            vertical = ZcashTheme.dimens.spacingSmall
        ),
    contentPaddingValues: PaddingValues = PaddingValues(all = 16.5.dp)
) {
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
                    .fillMaxWidth()
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
            buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                disabledContentColor = buttonColors.disabledContainerColor
            ),
        onClick = onClick,
    ) {
        Text(
            style = ZcashTheme.extendedTypography.buttonText,
            textAlign = TextAlign.Center,
            text = text.uppercase(),
            color =
                if (enabled) {
                    buttonColors.textColor
                } else {
                    buttonColors.disabledTextColor
                }
        )
    }
}

@Suppress("LongParameterList")
fun Modifier.shadow(
    contentColor: Color = Color.Black,
    strokeColor: Color = Color.Black,
    strokeWidth: Dp = 2.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val strokePaint = Paint()
            strokePaint.style = PaintingStyle.Stroke
            strokePaint.strokeWidth = strokeWidth.toPx()

            val contentPaint = Paint()
            strokePaint.style = PaintingStyle.Fill

            // Reusable inner function to be able to reach modifier and canvas properties
            fun drawShadowLayer(
                paint: Paint,
                color: Color,
                paddingWidth: Float
            ) {
                val frameworkPaint = paint.asFrameworkPaint()
                val spreadPixel = spread.toPx()
                val leftPixel = (0f - spreadPixel) + offsetX.toPx()
                val topPixel = (0f - spreadPixel) + offsetY.toPx()
                val rightPixel = (this.size.width + spreadPixel)
                val bottomPixel = (this.size.height + spreadPixel)

                frameworkPaint.color = color.toArgb()
                it.drawRoundRect(
                    left = leftPixel + paddingWidth,
                    top = topPixel + paddingWidth,
                    right = rightPixel - paddingWidth,
                    bottom = bottomPixel - paddingWidth,
                    radiusX = 0f,
                    radiusY = 0f,
                    paint
                )
            }

            // Draw stroke and then content paints
            drawShadowLayer(strokePaint, strokeColor, 0f)
            drawShadowLayer(contentPaint, contentColor, strokeWidth.toPx())
        }
    }
)

private enum class ButtonState { Pressed, Idle }

// TODO [#1346]: Rework not-recommended composed{}
// TODO [#1346]: https://github.com/Electric-Coin-Company/zashi-android/issues/1346
@Suppress("ModifierComposed")
fun Modifier.translationClick(
    translationX: Dp = 0.dp,
    translationY: Dp = 0.dp
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val translationXAnimated by animateFloatAsState(
        targetValue =
            if (buttonState == ButtonState.Pressed) {
                translationX.value
            } else {
                0f
            },
        label = "ClickTranslationXAnimation",
        animationSpec =
            tween(
                durationMillis = 100
            )
    )
    val translationYAnimated by animateFloatAsState(
        targetValue =
            if (buttonState == ButtonState.Pressed) {
                translationY.value
            } else {
                0f
            },
        label = "ClickTranslationYAnimation",
        animationSpec =
            tween(
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
                buttonState =
                    if (buttonState == ButtonState.Pressed) {
                        waitForUpOrCancellation()
                        ButtonState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonState.Pressed
                    }
            }
        }
}
