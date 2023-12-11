package co.electriccoin.zcash.ui.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import co.electriccoin.zcash.ui.design.BuildConfig
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
    // forceDarkMode takes precedence, then decides, based on the globally defined Gradle property
    // IS_APP_DARK_MODE_ENABLED, whether the device's system dark mode is on or off.
    val useDarkMode = forceDarkMode || (BuildConfig.IS_APP_DARK_MODE_ENABLED && isSystemInDarkTheme())

    val baseColors =
        if (useDarkMode) {
            DarkColorPalette
        } else {
            LightColorPalette
        }

    val extendedColors =
        if (useDarkMode) {
            DarkExtendedColorPalette
        } else {
            LightExtendedColorPalette
        }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
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
