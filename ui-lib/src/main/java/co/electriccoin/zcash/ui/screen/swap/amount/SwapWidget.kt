package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.colors.ZashiLightColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun SwapWidget(
    state: SwapWidgetState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ZashiDimensions.Radius.radiusXl),
        color = ZashiColors.Switcher.surfacePrimary
    ) {
        CustomTabLayout(
            modifier = Modifier.padding(2.dp),
            tabs = listOf(
                {
                    TabInternal(
                        mode = SwapWidgetState.Selection.SWAP,
                        state = state,
                    )
                },
                {
                    TabInternal(
                        mode = SwapWidgetState.Selection.PAY,
                        state = state,
                    )
                }
            ),
            indicator = { height, width ->
                Indicator(
                    height = height,
                    width = width,
                    state = state,
                )
            }
        )
    }
}

@Composable
private fun Indicator(
    height: Dp,
    width: Dp,
    state: SwapWidgetState,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {

        val finalOffset by animateDpAsState(
            if (state.selection == SwapWidgetState.Selection.SWAP) 0.dp else width,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .height(height)
                .width(width)
                .offset(x = finalOffset)
                .background(
                    shape = RoundedCornerShape(ZashiDimensions.Radius.radiusLg),
                    color = ZashiColors.Switcher.selectedBg,
                )
                .border(
                    border = BorderStroke(1.dp, ZashiColors.Switcher.selectedStroke),
                    shape = RoundedCornerShape(ZashiDimensions.Radius.radiusLg)
                )
        )
    }
}

@Composable
private fun CustomTabLayout(
    modifier: Modifier,
    tabs: List<@Composable () -> Unit>,
    indicator: @Composable (Dp, Dp) -> Unit
) {
    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->
        val measurables = tabs.mapIndexed { index, item -> subcompose(index, item)[0] }
        val placeables = mutableListOf<Placeable>()
        var maxWidth = 0
        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)
            placeables.add(placeable)
            if (placeable.width > maxWidth) {
                maxWidth = placeable.width
            }
        }

        val height = placeables.maxOf { it.measuredHeight }
        val totalWidth = placeables.maxOf { it.width } * placeables.size
        layout(width = totalWidth, height = height) {
            subcompose("indicator") {
                indicator(
                    height.toDp(),
                    maxWidth.toDp()
                )
            }[0].measure(
                constraints.copy(
                    minWidth = totalWidth,
                    maxWidth = totalWidth,
                    minHeight = height,
                    maxHeight = height
                )
            ).placeRelative(0, 0, .1f)

            for ((index, _) in tabs.withIndex()) {
                val child = subcompose("final_$index", tabs[index])[0]
                val finalPlaceable = child.measure(
                    constraints.copy(
                        minWidth = maxWidth,
                        maxWidth = maxWidth,
                    )
                )
                finalPlaceable.placeRelative(index * maxWidth, 0, 1f)
            }
        }
    }
}

@Composable
private fun TabInternal(
    mode: SwapWidgetState.Selection,
    state: SwapWidgetState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { state.onClick(mode) }
            .padding(
                vertical = 8.dp,
                horizontal = 20.dp
            )
    ) {
        val textColor by animateColorAsState(
            if (state.selection == mode) {
                ZashiColors.Switcher.defaultText orDark ZashiLightColors.Switcher.defaultText
            } else {
                ZashiColors.Switcher.selectedText orDark ZashiColors.Text.textSecondary
            }
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = when (mode) {
                SwapWidgetState.Selection.SWAP -> "Swap"
                SwapWidgetState.Selection.PAY -> "Pay"
            },
            color = textColor,
            style = ZashiTypography.textMd,
            fontWeight = FontWeight.Medium
        )
    }
}

data class SwapWidgetState(
    val selection: Selection,
    val onClick: (Selection) -> Unit
) {
    enum class Selection { SWAP, PAY }
}

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    var selection by remember { mutableStateOf(SwapWidgetState.Selection.SWAP) }
    BlankSurface {
        SwapWidget(
            SwapWidgetState(
                selection = selection,
                onClick = { selection = it }
            )
        )
    }
}
