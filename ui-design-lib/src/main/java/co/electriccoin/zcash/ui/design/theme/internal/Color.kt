@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.ExtendedColors

internal object Dark {
    val backgroundStart = Color(0xff243155)
    val backgroundEnd = Color(0xff29365A)

    val textHeaderOnBackground = Color(0xffCBDCF2)
    val textBodyOnBackground = Color(0xFF93A4BE)
    val textPrimaryButton = Color(0xFF0F2341)
    val textSecondaryButton = Color(0xFF0F2341)
    val textTertiaryButton = Color.White
    val textNavigationButton = Color.Black
    val textCaption = Color(0xFF68728B)
    val textChipIndex = Color(0xFFFFB900)

    val primaryButton = Color(0xFFFFB900)
    val primaryButtonPressed = Color(0xFFFFD800)
    val primaryButtonDisabled = Color(0x33F4B728)

    val secondaryButton = Color(0xFFA7C0D9)
    val secondaryButtonPressed = Color(0xFFC8DCEF)
    val secondaryButtonDisabled = Color(0x33C8DCEF)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xB0C3D2BA)

    val navigationButton = Color(0xFFA7C0D9)
    val navigationButtonPressed = Color(0xFFC8DCEF)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFF929bb3)

    val callout = Color(0xFFa7bed8)
    val onCallout = Color(0xFF3d698f)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    val addressHighlightBorder = Color(0xFF525252)
    val addressHighlightUnified = Color(0xFFFFD800)
    val addressHighlightSapling = Color(0xFF1BBFF6)
    val addressHighlightTransparent = Color(0xFF97999A)

    val dangerous = Color(0xFFEC0008)
    val onDangerous = Color(0xFFFFFFFF)

    val reference = Color(0xFF10A5FF)
}

internal object Light {
    val backgroundStart = Color(0xFFE3EFF9)
    val backgroundEnd = Color(0xFFD2E4F3)

    val textHeaderOnBackground = Color(0xff2D3747)
    val textBodyOnBackground = Color(0xFF7B8897)
    val textNavigationButton = Color(0xFF7B8897)
    val textPrimaryButton = Color(0xFFF2F7FC)
    val textSecondaryButton = Color(0xFF2E476E)
    val textTertiaryButton = Color(0xFF283559)
    val textCaption = Color(0xFF2D3747)
    val textChipIndex = Color(0xFFEE8592)

    // TODO [#159]: The button colors are wrong for light
    // TODO [#159]: https://github.com/zcash/secant-android-wallet/issues/159
    val primaryButton = Color(0xFF263357)
    val primaryButtonPressed = Color(0xFFFFD800)
    val primaryButtonDisabled = Color(0x33F4B728)

    val secondaryButton = Color(0xFFE8F3FA)
    val secondaryButtonPressed = Color(0xFFFAFBFD)
    val secondaryButtonDisabled = Color(0xFFE6EFF8)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xFFFFFFFF)

    val navigationButton = Color(0xFFE3EDF7)
    val navigationButtonPressed = Color(0xFFE3EDF7)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFFbeccdf)

    val callout = Color(0xFFe6f0f9)
    val onCallout = Color(0xFFa1b8d0)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    // TODO [#159]: The colors are wrong for light theme
    // TODO [#159]: https://github.com/zcash/secant-android-wallet/issues/159
    val addressHighlightBorder = Color(0xFF525252)
    val addressHighlightUnified = Color(0xFFFFD800)
    val addressHighlightSapling = Color(0xFF1BBFF6)
    val addressHighlightTransparent = Color(0xFF97999A)

    val dangerous = Color(0xFFEC0008)
    val onDangerous = Color(0xFFFFFFFF)

    val reference = Color(0xFF10A5FF)
}

internal val DarkColorPalette = darkColorScheme(
    primary = Dark.primaryButton,
    secondary = Dark.secondaryButton,
    onPrimary = Dark.textPrimaryButton,
    onSecondary = Dark.textSecondaryButton,
    surface = Dark.backgroundStart,
    onSurface = Dark.textBodyOnBackground,
    background = Dark.backgroundStart,
    onBackground = Dark.textBodyOnBackground
)

internal val LightColorPalette = lightColorScheme(
    primary = Light.primaryButton,
    secondary = Light.secondaryButton,
    onPrimary = Light.textPrimaryButton,
    onSecondary = Light.textSecondaryButton,
    surface = Light.backgroundStart,
    onSurface = Light.textBodyOnBackground,
    background = Light.backgroundStart,
    onBackground = Light.textBodyOnBackground
)

internal val DarkExtendedColorPalette = ExtendedColors(
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
    dangerous = Dark.dangerous,
    onDangerous = Dark.onDangerous,
    reference = Dark.reference
)

internal val LightExtendedColorPalette = ExtendedColors(
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
    dangerous = Light.dangerous,
    onDangerous = Light.onDangerous,
    reference = Light.reference
)

@Suppress("CompositionLocalAllowlist")
internal val LocalExtendedColors = staticCompositionLocalOf {
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
        dangerous = Color.Unspecified,
        onDangerous = Color.Unspecified,
        reference = Color.Unspecified
    )
}
