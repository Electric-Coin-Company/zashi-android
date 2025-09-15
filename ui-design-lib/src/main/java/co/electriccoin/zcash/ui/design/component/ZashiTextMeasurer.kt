package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

@Composable
fun measureTextStyle(
    style: TextStyle,
    text: String = "a",
): TextLayoutResult {
    val bulletTextMeasurer = rememberTextMeasurer()
    return bulletTextMeasurer.measure(text = text, style = style)
}

val IntSize.widthDp: Dp
    @Composable
    get() = with(LocalDensity.current) { width.toDp() }

val IntSize.heightDp: Dp
    @Composable
    get() = with(LocalDensity.current) { height.toDp() }
