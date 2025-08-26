@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import kotlin.math.roundToInt
import androidx.compose.material3.Slider as ComposeSlider

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ZashiSlider(
    state: SliderState,
    modifier: Modifier = Modifier
) {
    val colors =
        SliderDefaults.colors().copy(
            activeTrackColor = ZashiColors.Surfaces.bgAlt,
            inactiveTrackColor = ZashiColors.Utility.Gray.utilityGray200,
            inactiveTickColor = Color.Transparent,
            activeTickColor = Color.Transparent,
            disabledActiveTickColor = Color.Transparent,
            disabledInactiveTickColor = Color.Transparent,
        )
    val interactionSource = remember { MutableInteractionSource() }
    val range = (state.percentRange.first().toFloat() / 100f)..(state.percentRange.last().toFloat() / 100f)
    val selection = state.selectedPercent / 100f
    val steps = state.percentRange.count() - 2

    Box(modifier = modifier) {
        FakeBackground(colors)
        ComposeSlider(
            modifier = Modifier.height(THUMB_SIZE),
            interactionSource = interactionSource,
            value = selection,
            onValueChange = { state.onValueChange((it * 100f).roundToInt()) },
            steps = steps,
            valueRange = range,
            colors = colors,
            track = { sliderState ->
                SliderDefaults.Track(
                    modifier = Modifier.height(TRACK_HEIGHT),
                    colors = colors,
                    enabled = true,
                    sliderState = sliderState,
                    drawStopIndicator = null,
                    trackInsideCornerSize = 0.dp,
                    thumbTrackGapSize = 0.dp,
                )
            },
            thumb = {
                Thumb(
                    modifier = Modifier.size(THUMB_SIZE),
                    interactionSource = interactionSource
                )
            },
        )
    }
}

@Composable
private fun BoxScope.FakeBackground(colors: SliderColors) {
    Spacer(
        modifier =
            Modifier
                .height(TRACK_HEIGHT)
                .width(THUMB_SIZE)
                .background(
                    color = colors.activeTrackColor,
                    shape =
                        RoundedCornerShape(
                            topStart = (TRACK_HEIGHT / 2),
                            bottomStart = (TRACK_HEIGHT / 2)
                        )
                ).align(Alignment.CenterStart)
    )

    Spacer(
        modifier =
            Modifier
                .height(TRACK_HEIGHT)
                .width(THUMB_SIZE)
                .background(
                    color = colors.inactiveTrackColor,
                    shape =
                        RoundedCornerShape(
                            topEnd = (TRACK_HEIGHT / 2),
                            bottomEnd = (TRACK_HEIGHT / 2)
                        )
                ).align(Alignment.CenterEnd)
    )
}

@Immutable
data class SliderState(
    val selectedPercent: Int,
    val percentRange: IntProgression,
    val onValueChange: (Int) -> Unit
)

@Composable
private fun Thumb(
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    val interactions = remember { mutableStateListOf<Interaction>() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    Surface(
        modifier
            .hoverable(interactionSource = interactionSource),
        color = ZashiColors.Surfaces.bgPrimary,
        border = BorderStroke(1.5.dp, ZashiColors.Surfaces.bgAlt),
        shape = CircleShape,
        shadowElevation = 4.dp
    ) {
        // do nothing
    }
}

private val TRACK_HEIGHT = 8.dp
val THUMB_SIZE = 24.dp

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            var selectedPercent by remember { mutableIntStateOf(1) }
            ZashiSlider(
                state =
                    SliderState(
                        selectedPercent = selectedPercent,
                        percentRange = 1..5 step 1,
                        onValueChange = { selectedPercent = it }
                    ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
