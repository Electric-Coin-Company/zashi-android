package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun ZashiCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    colors: ZashiCircularProgressIndicatorColors =
        LocalZashiCircularProgressIndicatorColors.current
            ?: ZashiCircularProgressIndicatorDefaults.colors()
) {
    val animatedProgress by animateFloatAsState(
        progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    CircularProgressIndicator(
        modifier = modifier,
        color = colors.progressColor,
        trackColor = colors.trackColor,
        progress = { animatedProgress },
        gapSize = 0.dp,
        strokeWidth = 3.dp
    )
}

@Composable
fun ZashiCircularProgressIndicatorByPercent(
    progressPercent: Float,
    modifier: Modifier = Modifier,
    colors: ZashiCircularProgressIndicatorColors =
        LocalZashiCircularProgressIndicatorColors.current
            ?: ZashiCircularProgressIndicatorDefaults.colors()
) {
    ZashiCircularProgressIndicator(
        progress = progressPercent / 100f,
        modifier = modifier,
        colors = colors
    )
}

@Immutable
data class ZashiCircularProgressIndicatorColors(
    val progressColor: Color,
    val trackColor: Color
)

@Suppress("CompositionLocalAllowlist")
val LocalZashiCircularProgressIndicatorColors = compositionLocalOf<ZashiCircularProgressIndicatorColors?> { null }

object ZashiCircularProgressIndicatorDefaults {
    @Composable
    fun colors(
        progressColor: Color = ZashiColors.Utility.Purple.utilityPurple400,
        trackColor: Color = ZashiColors.Utility.Purple.utilityPurple50
    ) = ZashiCircularProgressIndicatorColors(
        progressColor = progressColor,
        trackColor = trackColor
    )
}
