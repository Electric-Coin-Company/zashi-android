package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun zashiVerticalGradient(
    startColor: Color = ZashiColors.Utility.WarningYellow.utilityOrange100,
    endColor: Color = ZashiColors.Surfaces.bgPrimary,
    startStop: Float = VERTICAL_GRADIENT_START_STOP,
    endStop: Float = VERTICAL_GRADIENT_END_STOP_LIGHT orDark VERTICAL_GRADIENT_END_STOP_DARK
) = Brush.verticalGradient(
    startStop to startColor,
    endStop to endColor,
)

const val VERTICAL_GRADIENT_START_STOP = .0f
const val VERTICAL_GRADIENT_END_STOP_DARK = .35f
const val VERTICAL_GRADIENT_END_STOP_LIGHT = .4f
