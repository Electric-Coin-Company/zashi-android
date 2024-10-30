package co.electriccoin.zcash.ui.common.compose

import android.content.res.Configuration
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.CaretScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

@Composable
@ExperimentalMaterial3Api
fun ZashiTooltipBox(
    tooltip: @Composable CaretScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    positionProvider: PopupPositionProvider = rememberTooltipPositionProvider(),
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = positionProvider,
        tooltip = tooltip,
        state = state,
        modifier = modifier,
        focusable = focusable,
        enableUserInput = enableUserInput,
        content = content
    )
}

@Composable
fun rememberTooltipPositionProvider(spacingBetweenTooltipAndAnchor: Dp = 8.dp): PopupPositionProvider {
    val tooltipAnchorSpacing =
        with(LocalDensity.current) {
            spacingBetweenTooltipAndAnchor.roundToPx()
        }
    return remember(tooltipAnchorSpacing) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2

                // Tooltip prefers to be above the anchor,
                // but if this causes the tooltip to overlap with the anchor
                // then we place it below the anchor

                var y = anchorBounds.bottom + tooltipAnchorSpacing
                if (y + popupContentSize.height > windowSize.height) {
                    y = anchorBounds.top - popupContentSize.height - tooltipAnchorSpacing
                }
                return IntOffset(x, y)
            }
        }
    }
}

@ExperimentalMaterial3Api
fun CacheDrawScope.drawCaretWithPath(
    density: Density,
    configuration: Configuration,
    containerColor: Color,
    caretProperties: CaretProperties = CaretProperties(caretHeight = 8.dp, caretWidth = 16.dp),
    anchorLayoutCoordinates: LayoutCoordinates?
): DrawResult {
    val path = Path()

    if (anchorLayoutCoordinates != null) {
        val caretHeightPx: Int
        val caretWidthPx: Int
        val screenWidthPx: Int
        val screenHeightPx: Int
        val tooltipAnchorSpacing: Int
        with(density) {
            caretHeightPx = caretProperties.caretHeight.roundToPx()
            caretWidthPx = caretProperties.caretWidth.roundToPx()
            screenWidthPx = configuration.screenWidthDp.dp.roundToPx()
            screenHeightPx = configuration.screenHeightDp.dp.roundToPx()
            tooltipAnchorSpacing = 4.dp.roundToPx()
        }
        val anchorBounds = anchorLayoutCoordinates.boundsInWindow()
        val anchorLeft = anchorBounds.left
        val anchorRight = anchorBounds.right
        val anchorMid = (anchorRight + anchorLeft) / 2
        val anchorWidth = anchorRight - anchorLeft
        val tooltipWidth = this.size.width
        val tooltipHeight = this.size.height

        val isCaretTop = (anchorBounds.bottom + tooltipAnchorSpacing + tooltipHeight) <= screenHeightPx
        val caretY =
            if (isCaretTop) {
                0f
            } else {
                tooltipHeight
            }

        val position =
            if (anchorMid + tooltipWidth / 2 > screenWidthPx) {
                val anchorMidFromRightScreenEdge =
                    screenWidthPx - anchorMid
                val caretX = tooltipWidth - anchorMidFromRightScreenEdge
                Offset(caretX, caretY)
            } else {
                val tooltipLeft =
                    anchorLeft - (this.size.width / 2 - anchorWidth / 2)
                val caretX = anchorMid - maxOf(tooltipLeft, 0f)
                Offset(caretX, caretY)
            }

        if (isCaretTop) {
            path.apply {
                moveTo(x = position.x, y = position.y)
                lineTo(x = position.x + caretWidthPx / 2, y = position.y)
                lineTo(x = position.x, y = position.y - caretHeightPx)
                lineTo(x = position.x - caretWidthPx / 2, y = position.y)
                close()
            }
        } else {
            path.apply {
                moveTo(x = position.x, y = position.y)
                lineTo(x = position.x + caretWidthPx / 2, y = position.y)
                lineTo(x = position.x, y = position.y + caretHeightPx.toFloat())
                lineTo(x = position.x - caretWidthPx / 2, y = position.y)
                close()
            }
        }
    }

    return onDrawWithContent {
        if (anchorLayoutCoordinates != null) {
            drawContent()
            drawPath(
                path = path,
                color = containerColor
            )
        }
    }
}
