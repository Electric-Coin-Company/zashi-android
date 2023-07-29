package co.electriccoin.zcash.ui.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import co.electriccoin.zcash.ui.design.theme.internal.ExtendedTypography
import co.electriccoin.zcash.ui.design.theme.internal.LightColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.LightExtendedColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.LocalExtendedColors
import co.electriccoin.zcash.ui.design.theme.internal.LocalExtendedTypography
import co.electriccoin.zcash.ui.design.theme.internal.Typography

@Composable
fun ZcashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val baseColors = if (darkTheme) {
//        DarkColorPalette
        LightColorPalette
    } else {
        LightColorPalette
    }

    val extendedColors = if (darkTheme) {
//        DarkExtendedColorPalette
        LightExtendedColorPalette
    } else {
        LightExtendedColorPalette
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        ProvideDimens {
            MaterialTheme(
                colorScheme = baseColors,
                typography = Typography,
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

    val typography: ExtendedTypography
        @Composable
        get() = LocalExtendedTypography.current

    // TODO [#808]: [Design system] Use Dimens across the app
    // TODO [#808]: https://github.com/zcash/secant-android-wallet/issues/808
    val dimens: Dimens
        @Composable
        get() = LocalDimens.current
}
