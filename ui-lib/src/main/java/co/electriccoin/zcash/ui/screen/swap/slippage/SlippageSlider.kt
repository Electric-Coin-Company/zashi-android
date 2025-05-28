package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.SliderState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.THUMB_SIZE
import co.electriccoin.zcash.ui.design.component.ZashiSlider
import co.electriccoin.zcash.ui.design.component.ZashiVerticallDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
fun SlippageSlider(
    state: SlippageSliderState,
    modifier: Modifier = Modifier,
) {
    val range = state.percentRange.first()..(state.percentRange.last + TEN_PERCENT) step state.percentRange.step
    val selection =
        when (state.selected) {
            is SlippageSliderState.Selection.Custom -> range.last
            is SlippageSliderState.Selection.ByPercent -> state.selected.percent
        }

    Column(
        modifier = modifier,
    ) {
        ZashiSlider(
            state =
                SliderState(
                    selectedPercent = selection,
                    percentRange = range,
                    onValueChange = {
                        state.onValueChange(
                            if (it == range.last) {
                                SlippageSliderState.Selection.Custom
                            } else {
                                SlippageSliderState.Selection.ByPercent(it)
                            }
                        )
                    }
                ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(6.dp)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
        ) {
            state.labelRange.forEach { percent ->
                val positionPercent = percent.toFloat() / (state.labelRange.last + TEN_PERCENT).toFloat()
                val displayPercent = percent / TEN_PERCENT
                Step(
                    isSelected =
                        state.selected is SlippageSliderState.Selection.ByPercent &&
                            state.selected.percent == percent,
                    text = "$displayPercent%",
                    modifier = Modifier.positionFromStartPercent(positionPercent)
                )
            }

            CustomStep(
                isSelected = state.selected is SlippageSliderState.Selection.Custom,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun Step(
    isSelected: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    val dividerColor by animateColorAsState(
        if (isSelected) ZashiColors.Text.textPrimary else ZashiColors.Utility.Gray.utilityGray200
    )

    val textColor by animateColorAsState(
        if (isSelected) ZashiColors.Text.textPrimary else ZashiColors.Text.textTertiary
    )

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZashiVerticallDivider(
            modifier = Modifier.height(12.dp),
            thickness = 1.dp,
            color = dividerColor
        )
        Spacer(6.dp)
        Text(
            text = text,
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun CustomStep(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val dividerColor by animateColorAsState(
        if (isSelected) {
            ZashiColors.Text.textPrimary
        } else {
            ZashiColors.Utility.Gray.utilityGray200
        }
    )

    val textColor by animateColorAsState(
        if (isSelected) {
            ZashiColors.Text.textPrimary
        } else {
            ZashiColors.Text.textTertiary
        }
    )

    Column(
        modifier,
        horizontalAlignment = Alignment.End
    ) {
        ZashiVerticallDivider(
            modifier =
                Modifier
                    .height(12.dp)
                    .padding(end = THUMB_SIZE / 2),
            thickness = 1.dp,
            color = dividerColor
        )
        Spacer(6.dp)
        Text(
            text = "Custom",
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun Modifier.positionFromStartPercent(percent: Float) =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val parentWidth = constraints.maxWidth - THUMB_SIZE.roundToPx()
        val xPosition = (parentWidth * percent).toInt()
        val width = placeable.width
        val height = placeable.height
        layout(width, height) {
            placeable.placeRelative((xPosition - width / 2) + (THUMB_SIZE / 2).roundToPx(), 0)
        }
    }

@Immutable
data class SlippageSliderState(
    val selected: Selection,
    val percentRange: IntProgression,
    val labelRange: IntProgression,
    val onValueChange: (Selection) -> Unit
) {
    sealed interface Selection {
        data class ByPercent(
            val percent: Int
        ) : Selection

        data object Custom : Selection
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            var selected: SlippageSliderState.Selection by remember {
                mutableStateOf(SlippageSliderState.Selection.ByPercent(0))
            }
            Column {
                SlippageSlider(
                    state =
                        SlippageSliderState(
                            selected = selected,
                            percentRange = 0..400 step ONE_PERCENT,
                            labelRange = 0..400 step TEN_PERCENT,
                            onValueChange = { selected = it }
                        ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(24.dp)
                Text(
                    text = "Selection: $selected"
                )
            }
        }
    }

private const val ONE_PERCENT = 10
private const val TEN_PERCENT = ONE_PERCENT * 10
