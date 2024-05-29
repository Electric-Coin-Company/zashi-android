package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Blank background")
@Composable
private fun BlankSurfacePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Text(
                text = "Test text on the blank app background",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview("Grid background")
@Composable
private fun GridSurfacePreview() {
    ZcashTheme(forceDarkMode = false) {
        GridSurface {
            Text(
                text = "Test text on the grip app background",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BlankSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = ZcashTheme.colors.backgroundColor,
        shape = RectangleShape,
        content = content,
        modifier = modifier
    )
}

@Composable
fun GridSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RectangleShape,
        content = content,
        modifier =
            modifier.then(
                Modifier
                    .gridBackground(
                        backgroundColor = ZcashTheme.colors.backgroundColor,
                        gridSize = ZcashTheme.dimens.gridCellSize,
                        gridColor = ZcashTheme.colors.gridColor,
                        gridLineWidth = ZcashTheme.dimens.gridLineWidth
                    )
            )
    )
}

fun Modifier.gridBackground(
    backgroundColor: Color,
    gridSize: Dp,
    gridColor: Color,
    gridLineWidth: Dp
): Modifier {
    return then(
        background(backgroundColor)
            .drawBehind {
                val gridWidth = size.width
                val gridHeight = size.height

                val stepX = gridSize.toPx()
                val stepY = gridSize.toPx()

                val xSteps = (gridWidth / stepX).toInt()
                val ySteps = (gridHeight / stepY).toInt()

                for (i in 0..xSteps) {
                    val x = i * stepX
                    drawLine(
                        start = Offset(x, 0f),
                        end = Offset(x, gridHeight),
                        color = gridColor,
                        strokeWidth = gridLineWidth.toPx()
                    )
                }
                for (i in 0..ySteps) {
                    val y = i * stepY
                    drawLine(
                        start = Offset(0f, y),
                        end = Offset(gridWidth, y),
                        color = gridColor,
                        strokeWidth = gridLineWidth.toPx()
                    )
                }
            }
    )
}
