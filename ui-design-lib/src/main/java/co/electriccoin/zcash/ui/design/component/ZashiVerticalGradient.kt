package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun zashiVerticalGradient(
    startColor: Color = ZashiColors.Utility.WarningYellow.utilityOrange100,
    endColor: Color = ZashiColors.Surfaces.bgPrimary
) = Brush.verticalGradient(
    START_STOP to startColor,
    (END_STOP_LIGHT orDark END_STOP_DARK) to endColor,
)

private const val START_STOP = .0f
private const val END_STOP_DARK = .35f
private const val END_STOP_LIGHT = .4f