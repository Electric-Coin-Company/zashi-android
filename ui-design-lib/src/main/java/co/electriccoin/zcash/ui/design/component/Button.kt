package co.electriccoin.zcash.ui.design.component

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun ButtonComposablePreview() {
    ZcashTheme(darkTheme = true) {
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

@Suppress("LongParameterList")
@Composable
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
            text = text,
            color = textColor
        )
    }
}

@Composable
fun SecondaryButton(
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
        colors = buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            text = text,
            color = MaterialTheme.colorScheme.onSecondary
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
        Text(style = MaterialTheme.typography.labelLarge, text = text, color = MaterialTheme.colorScheme.onSecondary)
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
