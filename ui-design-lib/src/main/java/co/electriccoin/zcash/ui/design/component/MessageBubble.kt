package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

private const val ARROW_PADDING = 16
private const val ARROW_WIDTH = 24
private const val ARROW_HEIGHT = 8
private const val COMPONENT_MIN_WIDTH = ARROW_WIDTH * 3

@Preview(showBackground = true)
@Composable
private fun BubbleWithTextPreview() {
    ZcashTheme {
        BubbleMessage(backgroundColor = ZcashTheme.colors.dividerColor) {
            Text(
                text = "TextTextTextText",
                fontSize = 16.sp,
                modifier = Modifier.padding(ZcashTheme.dimens.spacingDefault)
            )
        }
    }
}

@Preview("Small content and left arrow preview", showBackground = true)
@Composable
private fun BubbleMinSizePreview() {
    ZcashTheme {
        BubbleMessage(arrowAlignment = BubbleArrowAlignment.BottomLeft) {
            Text(
                text = "T",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.padding(ZcashTheme.dimens.spacingDefault)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BubbleWithTextFieldPreview() {
    ZcashTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            BubbleMessage {
                @OptIn(ExperimentalFoundationApi::class)
                FormTextField(
                    value = "FormTextField",
                    onValueChange = {},
                    modifier = Modifier.padding(ZcashTheme.dimens.spacingDefault),
                    withBorder = false
                )
            }
        }
    }
}

@Composable
fun BubbleMessage(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    borderStroke: BorderStroke = BorderStroke(1.dp, ZcashTheme.colors.textFieldFrame),
    arrowAlignment: BubbleArrowAlignment = BubbleArrowAlignment.BottomRight,
    content: @Composable () -> Unit
) {
    val shape = createBubbleShape(arrowAlignment)
    Surface(
        modifier =
            modifier
                .clip(shape)
                .border(shape = shape, border = borderStroke)
                .background(backgroundColor)
                .sizeIn(minWidth = COMPONENT_MIN_WIDTH.dp) // prevent collapsing when content is too small
                .padding(bottom = ARROW_HEIGHT.dp), // compensate component height to center content
        color = backgroundColor
    ) {
        content()
    }
}

@Composable
private fun createBubbleShape(arrowAlignment: BubbleArrowAlignment): Shape {
    val density = LocalDensity.current
    return GenericShape { size, _ ->
        with(density) {
            val arrowWidth = ARROW_WIDTH.dp.toPx()
            val arrowHeight = ARROW_HEIGHT.dp.toPx()
            val arrowPadding = ARROW_PADDING.dp.toPx()

            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - arrowHeight)

            when (arrowAlignment) {
                BubbleArrowAlignment.BottomLeft -> {
                    lineTo(arrowWidth + arrowPadding, size.height - arrowHeight)
                    lineTo(arrowPadding, size.height)
                    lineTo(arrowPadding, size.height - arrowHeight)
                }
                BubbleArrowAlignment.BottomRight -> {
                    lineTo(size.width - arrowPadding, size.height - arrowHeight)
                    lineTo(size.width - arrowPadding, size.height)
                    lineTo(size.width - (arrowWidth + arrowPadding), size.height - arrowHeight)
                }
            }

            lineTo(0f, size.height - arrowHeight)
            close()
        }
    }
}

enum class BubbleArrowAlignment {
    BottomLeft,
    BottomRight
}
