package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun rememberZashiShimmer() =
    rememberShimmer(
        ShimmerBounds.View,
        LocalShimmerTheme.current.copy(
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 750,
                            easing = LinearEasing,
                            delayMillis = 450,
                        ),
                    repeatMode = RepeatMode.Restart,
                )
        )
    )

@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = ZashiColors.Surfaces.bgSecondary
) {
    Box(
        modifier =
            modifier
                .size(size)
                .background(color, CircleShape)
    )
}

@Composable
fun ShimmerRectangle(
    width: Dp = 40.dp,
    height: Dp = 20.dp,
    color: Color = ZashiColors.Surfaces.bgSecondary
) {
    Box(
        modifier =
            Modifier
                .width(width)
                .height(height)
                .background(color, RoundedCornerShape(ZashiDimensions.Radius.radiusSm))
    )
}

@Composable
fun ShimmerRectangle(
    modifier: Modifier = Modifier,
    color: Color = ZashiColors.Surfaces.bgSecondary
) {
    Box(
        modifier =
            modifier
                .background(color, RoundedCornerShape(ZashiDimensions.Radius.radiusSm))
    )
}
