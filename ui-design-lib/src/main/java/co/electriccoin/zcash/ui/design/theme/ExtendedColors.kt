package co.electriccoin.zcash.ui.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val surfaceEnd: Color,
    val onBackgroundHeader: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val callout: Color,
    val onCallout: Color,
    val progressBarSmall: Color,
    val progressBarScreen: Color,
    val chipIndex: Color,
    val textCommon: Color,
    val textFieldHint: Color,
    val textDescription: Color,
    val textPending: Color,
    val layoutStroke: Color,
    val overlay: Color,
    val highlight: Color,
    val dangerous: Color,
    val onDangerous: Color,
    val reference: Color,
    val disabledButtonColor: Color,
    val disabledButtonTextColor: Color,
    val buttonShadowColor: Color,
    val screenTitleColor: Color,
    val aboutTextColor: Color,
    val welcomeAnimationColor: Color,
    val complementaryColor: Color,
    val dividerColor: Color,
    val darkDividerColor: Color,
    val tabTextColor: Color,
) {
    @Composable
    fun surfaceGradient() =
        Brush.verticalGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.surface,
                    ZcashTheme.colors.surfaceEnd
                )
        )
}
