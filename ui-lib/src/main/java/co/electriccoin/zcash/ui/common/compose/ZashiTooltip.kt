package co.electriccoin.zcash.ui.common.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiAnimatedTooltip(
    isVisible: Boolean,
    title: StringResource,
    message: StringResource,
    onDismissRequest: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = enterTransition(),
        exit = exitTransition(),
    ) {
        ZashiTooltip(title, message, onDismissRequest)
    }
}

@Composable
fun ZashiAnimatedTooltip(
    visibleState: MutableTransitionState<Boolean>,
    title: StringResource,
    message: StringResource,
    onDismissRequest: () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enterTransition(),
        exit = exitTransition(),
    ) {
        ZashiTooltip(title, message, onDismissRequest)
    }
}

@Composable
fun ZashiTooltip(
    title: StringResource,
    message: StringResource,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showCaret: Boolean = true,
) {
    Column(
        modifier = modifier.padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showCaret) {
            Box(
                modifier =
                    Modifier
                        .width(16.dp)
                        .height(8.dp)
                        .background(ZashiColors.HintTooltips.surfacePrimary, TriangleShape)
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .background(ZashiColors.HintTooltips.surfacePrimary, RoundedCornerShape(8.dp))
                .padding(start = 12.dp, bottom = 12.dp),
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        color = ZashiColors.Text.textLight,
                        style = ZashiTypography.textMd,
                        text = title.getValue()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        color = ZashiColors.Text.textLightSupport,
                        style = ZashiTypography.textSm,
                        text = message.getValue()
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        painter = painterResource(R.drawable.ic_exchange_rate_unavailable_dialog_close),
                        contentDescription = "",
                        tint = ZashiColors.HintTooltips.defaultFg
                    )
                }
            }
        }
    }
}

@Composable
private fun exitTransition() =
    fadeOut() +
        scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
        slideOutVertically()

@Composable
private fun enterTransition() =
    fadeIn() +
        slideInVertically(spring(stiffness = Spring.StiffnessHigh)) +
        scaleIn(spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy))

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        ZashiTooltip(
            title = stringRes(R.string.exchange_rate_unavailable_title),
            message = stringRes(R.string.exchange_rate_unavailable_subtitle),
            onDismissRequest = {}
        )
    }

private val TriangleShape =
    GenericShape { size, _ ->

        // 1) Start at the top center
        moveTo(size.width / 2f, 0f)

        // 2) Draw a line to the bottom right corner
        lineTo(size.width, size.height)

        // 3) Draw a line to the bottom left corner and implicitly close the shape
        lineTo(0f, size.height)
    }
