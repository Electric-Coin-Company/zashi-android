package cash.z.ecc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Dark.primaryButton,
    secondary = Dark.secondaryButton,
    onPrimary = Dark.textPrimaryButton,
    onSecondary = Dark.textSecondaryButton,
    surface = Dark.backgroundStart,
    onSurface = Dark.textBodyOnBackground,
    background = Dark.backgroundStart,
    onBackground = Dark.textBodyOnBackground,
)

private val LightColorPalette = lightColorScheme(
    primary = Light.primaryButton,
    secondary = Light.secondaryButton,
    onPrimary = Light.textPrimaryButton,
    onSecondary = Light.textSecondaryButton,
    surface = Light.backgroundStart,
    onSurface = Light.textBodyOnBackground,
    background = Light.backgroundStart,
    onBackground = Light.textBodyOnBackground,
)

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
    val addressHighlightViewing: Color,
    val dangerous: Color,
    val onDangerous: Color
) {
    @Composable
    fun surfaceGradient() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            ZcashTheme.colors.surfaceEnd
        )
    )
}

val DarkExtendedColorPalette = ExtendedColors(
    surfaceEnd = Dark.backgroundEnd,
    onBackgroundHeader = Dark.textHeaderOnBackground,
    tertiary = Dark.tertiaryButton,
    onTertiary = Dark.textTertiaryButton,
    callout = Dark.callout,
    onCallout = Dark.onCallout,
    progressStart = Dark.progressStart,
    progressEnd = Dark.progressEnd,
    progressBackground = Dark.progressBackground,
    chipIndex = Dark.textChipIndex,
    overlay = Dark.overlay,
    highlight = Dark.highlight,
    addressHighlightBorder = Dark.addressHighlightBorder,
    addressHighlightUnified = Dark.addressHighlightUnified,
    addressHighlightSapling = Dark.addressHighlightSapling,
    addressHighlightTransparent = Dark.addressHighlightTransparent,
    addressHighlightViewing = Dark.addressHighlightViewing,
    dangerous = Dark.dangerous,
    onDangerous = Dark.onDangerous
)

val LightExtendedColorPalette = ExtendedColors(
    surfaceEnd = Light.backgroundEnd,
    onBackgroundHeader = Light.textHeaderOnBackground,
    tertiary = Light.tertiaryButton,
    onTertiary = Light.textTertiaryButton,
    callout = Light.callout,
    onCallout = Light.onCallout,
    progressStart = Light.progressStart,
    progressEnd = Light.progressEnd,
    progressBackground = Light.progressBackground,
    chipIndex = Light.textChipIndex,
    overlay = Light.overlay,
    highlight = Light.highlight,
    addressHighlightBorder = Light.addressHighlightBorder,
    addressHighlightUnified = Light.addressHighlightUnified,
    addressHighlightSapling = Light.addressHighlightSapling,
    addressHighlightTransparent = Light.addressHighlightTransparent,
    addressHighlightViewing = Light.addressHighlightViewing,
    dangerous = Light.dangerous,
    onDangerous = Light.onDangerous
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        surfaceEnd = Color.Unspecified,
        onBackgroundHeader = Color.Unspecified,
        tertiary = Color.Unspecified,
        onTertiary = Color.Unspecified,
        callout = Color.Unspecified,
        onCallout = Color.Unspecified,
        progressStart = Color.Unspecified,
        progressEnd = Color.Unspecified,
        progressBackground = Color.Unspecified,
        chipIndex = Color.Unspecified,
        overlay = Color.Unspecified,
        highlight = Color.Unspecified,
        addressHighlightBorder = Color.Unspecified,
        addressHighlightUnified = Color.Unspecified,
        addressHighlightSapling = Color.Unspecified,
        addressHighlightTransparent = Color.Unspecified,
        addressHighlightViewing = Color.Unspecified,
        dangerous = Color.Unspecified,
        onDangerous = Color.Unspecified
    )
}

@Composable
fun ZcashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val baseColors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val extendedColors = if (darkTheme) {
        DarkExtendedColorPalette
    } else {
        LightExtendedColorPalette
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = baseColors,
            typography = Typography,
            content = content
        )
    }
}

// Use with eg. ZcashTheme.colors.tertiary
object ZcashTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val typography: ExtendedTypography
        @Composable
        get() = LocalExtendedTypography.current
}
