package co.electriccoin.zcash.ui.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import co.electriccoin.zcash.ui.design.theme.colors.DarkZashiColorsInternal
import co.electriccoin.zcash.ui.design.theme.colors.LightZashiColorsInternal
import co.electriccoin.zcash.ui.design.theme.colors.LocalZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.LocalZashiDimensions
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensionsInternal
import co.electriccoin.zcash.ui.design.theme.internal.DarkColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.DarkExtendedColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.ExtendedTypography
import co.electriccoin.zcash.ui.design.theme.internal.LightColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.LightExtendedColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.LocalExtendedColors
import co.electriccoin.zcash.ui.design.theme.internal.LocalExtendedTypography
import co.electriccoin.zcash.ui.design.theme.internal.LocalTypographies
import co.electriccoin.zcash.ui.design.theme.internal.PrimaryTypography
import co.electriccoin.zcash.ui.design.theme.internal.Typography
import co.electriccoin.zcash.ui.design.theme.typography.LocalZashiTypography
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypographyInternal

/**
 * Commonly used top level app theme definition
 *
 * @param forceDarkMode Set this to true to force the app to use the dark mode theme, which is helpful, e.g.,
 * for the compose previews.
 */
@Composable
fun ZcashTheme(
    forceDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDarkMode = forceDarkMode || isSystemInDarkTheme()
    val baseColors = if (useDarkMode) DarkColorPalette else LightColorPalette
    val extendedColors = if (useDarkMode) DarkExtendedColorPalette else LightExtendedColorPalette
    val zashiColors = if (useDarkMode) DarkZashiColorsInternal else LightZashiColorsInternal

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalZashiColors provides zashiColors,
        LocalZashiDimensions provides ZashiDimensionsInternal,
        LocalZashiTypography provides ZashiTypographyInternal
    ) {
        ProvideDimens {
            MaterialTheme(
                colorScheme = baseColors,
                typography = PrimaryTypography,
                content = content
            )
        }
    }
}

// Use with eg. ZcashTheme.colors.tertiary
object ZcashTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val typography: Typography
        @Composable
        get() = LocalTypographies.current

    val extendedTypography: ExtendedTypography
        @Composable
        get() = LocalExtendedTypography.current

    // TODO [#808]: [Design system] Use Dimens across the app
    // TODO [#808]: https://github.com/Electric-Coin-Company/zashi-android/issues/808
    val dimens: Dimens
        @Composable
        get() = localDimens.current
}
