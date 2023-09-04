@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.ExtendedColors

internal object Dark {
    val backgroundStart = Color(0xFF000000)
    val backgroundEnd = Color(0xFF000000)

    val textHeaderOnBackground = Color(0xFFFFFFFF)
    val textBodyOnBackground = Color(0xFFFFFFFF)
    val textPrimaryButton = Color(0xFF000000)
    val textSecondaryButton = Color(0xFF000000)
    val textTertiaryButton = Color.White
    val textNavigationButton = Color.Black
    val textCaption = Color(0xFFFFFFFF)
    val textChipIndex = Color(0xFFFFB900)

    val primaryButton = Color(0xFFFFFFFF)
    val primaryButtonPressed = Color(0xFFFFFFFF)
    val primaryButtonDisabled = Color(0xFFFFFFFF)

    val secondaryButton = Color(0xFFFFFFFF)
    val secondaryButtonPressed = Color(0xFFFFFFFF)
    val secondaryButtonDisabled = Color(0xFFFFFFFF)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xFFFFFFFF)

    val navigationButton = Color(0xFFFFFFFF)
    val navigationButtonPressed = Color(0xFFFFFFFF)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFF929bb3)

    val callout = Color(0xFFFFFFFF)
    val onCallout = Color(0xFFFFFFFF)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    val addressHighlightBorder = Color(0xFF525252)
    val addressHighlightUnified = Color(0xFFFFD800)
    val addressHighlightSapling = Color(0xFF1BBFF6)
    val addressHighlightTransparent = Color(0xFF97999A)

    val dangerous = Color(0xFFEC0008)
    val onDangerous = Color(0xFFFFFFFF)

    val reference = Color(0xFF10A5FF)

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)

    val buttonShadowColor = Color(0xFFFFFFFF)

    // to be added later for dark theme
    val screenTitleColor = Color.Unspecified
}

internal object Light {
    val backgroundStart = Color(0xFFFFFFFF)
    val backgroundEnd = Color(0xFFFFFFFF)

    val textHeaderOnBackground = Color(0xFF000000)
    val textBodyOnBackground = Color(0xFF000000)
    val textNavigationButton = Color(0xFFFFFFFF)
    val textPrimaryButton = Color(0xFFFFFFFF)
    val textSecondaryButton = Color(0xFF000000)
    val textTertiaryButton = Color(0xFF000000)
    val textCaption = Color(0xFF000000)
    val textChipIndex = Color(0xFFEE8592)

    // TODO [#159]: The button colors are wrong for light
    // TODO [#159]: https://github.com/zcash/secant-android-wallet/issues/159
    val primaryButton = Color(0xFF000000)
    val primaryButtonPressed = Color(0xFF000000)
    val primaryButtonDisabled = Color(0xFF000000)

    val secondaryButton = Color(0xFFFFFFFF)
    val secondaryButtonPressed = Color(0xFFFFFFFF)
    val secondaryButtonDisabled = Color(0xFFFFFFFF)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xFFFFFFFF)

    val navigationButton = Color(0xFFFFFFFF)
    val navigationButtonPressed = Color(0xFFFFFFFF)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFFFFFFFF)

    val callout = Color(0xFFFFFFFF)
    val onCallout = Color(0xFFFFFFFF)

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

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)
    val buttonShadowColor = Color(0xFF000000)

    val screenTitleColor = Color(0xFF040404)
}

internal val DarkColorPalette = darkColorScheme(
    primary = Dark.primaryButton,
    secondary = Dark.secondaryButton,
    onPrimary = Dark.textPrimaryButton,
    onSecondary = Dark.textSecondaryButton,
    surface = Dark.backgroundStart,
    onSurface = Dark.textBodyOnBackground,
    background = Dark.backgroundStart,
    onBackground = Dark.textBodyOnBackground,
)

internal val LightColorPalette = lightColorScheme(
    primary = Light.primaryButton,
    secondary = Light.secondaryButton,
    onPrimary = Light.textPrimaryButton,
    onSecondary = Light.textSecondaryButton,
    surface = Light.backgroundStart,
    onSurface = Light.textBodyOnBackground,
    background = Light.backgroundStart,
    onBackground = Light.textBodyOnBackground,
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
    disabledButtonTextColor = Dark.disabledButtonTextColor,
    disabledButtonColor = Dark.disabledButtonColor,
    reference = Dark.reference,
    buttonShadowColor = Dark.buttonShadowColor,
    screenTitleColor = Dark.screenTitleColor
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
    disabledButtonTextColor = Light.disabledButtonTextColor,
    disabledButtonColor = Light.disabledButtonColor,
    reference = Light.reference,
    buttonShadowColor = Light.buttonShadowColor,
    screenTitleColor = Light.screenTitleColor,
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
        disabledButtonTextColor = Color.Unspecified,
        disabledButtonColor = Color.Unspecified,
        reference = Color.Unspecified,
        buttonShadowColor = Color.Unspecified,
        screenTitleColor = Color.Unspecified,
    )
}
