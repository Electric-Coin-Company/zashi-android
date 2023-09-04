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
    val progressStart: Color,
    val progressEnd: Color,
    val progressBackground: Color,
    val chipIndex: Color,
    val overlay: Color,
    val highlight: Color,
    val addressHighlightBorder: Color,
    val addressHighlightUnified: Color,
    val addressHighlightSapling: Color,
    val addressHighlightTransparent: Color,
    val dangerous: Color,
    val onDangerous: Color,
    val reference: Color,
    val disabledButtonColor: Color,
    val disabledButtonTextColor: Color,
    val buttonShadowColor: Color,
) {
    @Composable
    fun surfaceGradient() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            ZcashTheme.colors.surfaceEnd
        )
    )
}
