package co.electriccoin.zcash.ui.screen.home.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import kotlinx.coroutines.delay

@Suppress("MagicNumber")
@Composable
fun HomeMessage(state: HomeMessageState?) {
    val cutoutHeight = 16.dp
    var normalizedState: HomeMessageState? by remember { mutableStateOf(state) }
    var isVisible by remember { mutableStateOf(state != null) }
    val bottomCornerSize by animateDpAsState(
        if (isVisible) cutoutHeight else 0.dp,
        animationSpec = tween(350)
    )

    Box(
        modifier =
            Modifier
                .background(Color.Gray)
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(cutoutHeight)
                    .zIndex(2f)
                    .bottomOnlyShadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                        backgroundColor = ZashiColors.Surfaces.bgPrimary
                    ),
        )

        AnimatedVisibility(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .zIndex(0f),
            visible = isVisible,
            enter = expandIn(animationSpec = tween(350)),
            exit = shrinkOut(animationSpec = tween(350))
        ) {
            when (normalizedState) {
                is WalletBackupMessageState ->
                    WalletBackupMessage(
                        state = normalizedState as WalletBackupMessageState,
                        contentPadding =
                            PaddingValues(
                                vertical = cutoutHeight
                            )
                    )

                null -> {
                    // do nothing
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(cutoutHeight)
                    .zIndex(1f)
                    .align(Alignment.BottomCenter)
                    .topOnlyShadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(topStart = bottomCornerSize, topEnd = bottomCornerSize),
                        backgroundColor = ZashiColors.Surfaces.bgPrimary
                    ),
        )
    }

    LaunchedEffect(state) {
        if (state != null) {
            normalizedState = state
            isVisible = true
        } else {
            isVisible = false
            delay(350)
            normalizedState = null
        }
    }
}

private fun Modifier.bottomOnlyShadow(
    elevation: Dp,
    shape: Shape,
    backgroundColor: Color,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier =
    this
        .drawWithCache {
            //  bottom shadow offset in Px based on elevation
            val bottomOffsetPx = elevation.toPx()
            // Adjust the size to extend the bottom by the bottom shadow offset
            val adjustedSize = Size(size.width, size.height + bottomOffsetPx)
            val outline = shape.createOutline(adjustedSize, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }
            onDrawWithContent {
                clipPath(path, ClipOp.Intersect) {
                    this@onDrawWithContent.drawContent()
                }
            }
        }
        .shadow(elevation, shape, clip, ambientColor, spotColor)
        .background(
            backgroundColor,
            shape
        )

private fun Modifier.topOnlyShadow(
    elevation: Dp,
    shape: Shape,
    backgroundColor: Color,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier =
    this
        .drawWithCache {
            // Adjust the size to extend the bottom by the bottom shadow offset
            val adjustedSize = Size(size.width, size.height)
            val outline = shape.createOutline(adjustedSize, layoutDirection, this)
            val path = Path().apply { addOutline(outline) }
            onDrawWithContent {
                clipPath(path, ClipOp.Intersect) {
                    this@onDrawWithContent.drawContent()
                }
            }
        }
        .shadow(elevation, shape, clip, ambientColor, spotColor)
        .background(
            backgroundColor,
            shape
        )
