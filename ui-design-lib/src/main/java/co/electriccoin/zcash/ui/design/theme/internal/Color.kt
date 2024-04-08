@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.ExtendedColors

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

// TODO [#1091]: Clear unused color resources
// TODO [#1091]: https://github.com/Electric-Coin-Company/zashi-android/issues/1091

internal object Dark {
    val backgroundStart = Color(0xFF000000)
    val backgroundEnd = Color(0xFF000000)

    val textHeaderOnBackground = Color(0xFFFFFFFF)
    val textBodyOnBackground = Color(0xFFFFFFFF)
    val textPrimaryButton = Color(0xFF000000)
    val textSecondaryButton = Color(0xFF000000)
    val textTertiaryButton = Color.White
    val textCommon = Color(0xFFFFFFFF)
    val textMedium = Color(0xFF353535)
    val textDisabled = Color(0xFFB7B7B7)
    val textChipIndex = Color(0xFFFFB900)
    val textFieldFrame = Color(0xFF231F20)
    val textFieldError = Color(0xFFFF0000)
    val textFieldHint = Color(0xFFB7B7B7)
    val textDescription = Color(0xFF777777)
    val textProgress = Color(0xFF8B8A8A)

    val aboutTextColor = Color(0xFF4E4E4E)
    val screenTitleColor = Color(0xFF040404)
    val welcomeAnimationColor = Color(0xFF231F20)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFF000000)
    val tabTextColor = Color(0xFF040404)
    val layoutStroke = Color(0xFFFFFFFF)
    val panelBackgroundColor = Color(0xFFEAEAEA)

    val primaryButton = Color(0xFFFFFFFF)
    val secondaryButton = Color(0xFFFFFFFF)
    val tertiaryButton = Color.Transparent

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarSmallDark = textBodyOnBackground
    val circularProgressBarScreen = Color(0xFFFFFFFF)
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor
    val restoringTopAppBarColor = Color(0xFF8A8888)

    val callout = Color(0xFFFFFFFF)
    val onCallout = Color(0xFFFFFFFF)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    val reference = Color(0xFFFFFFFF)

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)

    val buttonShadowColor = Color(0xFFFFFFFF)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = Color(0xFFF40202)
}

internal object Light {
    val backgroundStart = Color(0xFFFFFFFF)
    val backgroundEnd = Color(0xFFFFFFFF)

    val textHeaderOnBackground = Color(0xFF000000)
    val textBodyOnBackground = Color(0xFF000000)
    val textPrimaryButton = Color(0xFFFFFFFF)
    val textSecondaryButton = Color(0xFF000000)
    val textTertiaryButton = Color(0xFF000000)
    val textCommon = Color(0xFF000000)
    val textMedium = Color(0xFF353535)
    val textDisabled = Color(0xFFB7B7B7)
    val textFieldFrame = Color(0xFF231F20)
    val textFieldError = Color(0xFFCD0002)
    val textFieldHint = Color(0xFFB7B7B7)
    val textChipIndex = Color(0xFFEE8592)
    val textDescription = Color(0xFF777777)
    val textProgress = Color(0xFF8B8A8A)

    val screenTitleColor = Color(0xFF040404)
    val aboutTextColor = Color(0xFF4E4E4E)
    val welcomeAnimationColor = Color(0xFF231F20)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFF000000)
    val tabTextColor = Color(0xFF040404)
    val layoutStroke = Color(0xFF000000)
    val panelBackgroundColor = Color(0xFFEAEAEA)

    val primaryButton = Color(0xFF000000)
    val secondaryButton = Color(0xFFFFFFFF)
    val tertiaryButton = Color.Transparent

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarScreen = Color(0xFF000000)
    val circularProgressBarSmallDark = textBodyOnBackground
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor
    val restoringTopAppBarColor = Color(0xFF8A8888)

    val callout = Color(0xFFFFFFFF)
    val onCallout = Color(0xFFFFFFFF)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    val reference = Color(0xFF000000)

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)
    val buttonShadowColor = Color(0xFF000000)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = Color(0xFFF40202)
}

internal val DarkColorPalette =
    darkColorScheme(
        primary = Dark.primaryButton,
        secondary = Dark.secondaryButton,
        onPrimary = Dark.textPrimaryButton,
        onSecondary = Dark.textSecondaryButton,
        surface = Dark.backgroundStart,
        onSurface = Dark.textBodyOnBackground,
        background = Dark.backgroundStart,
        onBackground = Dark.textBodyOnBackground,
    )

internal val LightColorPalette =
    lightColorScheme(
        primary = Light.primaryButton,
        secondary = Light.secondaryButton,
        onPrimary = Light.textPrimaryButton,
        onSecondary = Light.textSecondaryButton,
        surface = Light.backgroundStart,
        onSurface = Light.textBodyOnBackground,
        background = Light.backgroundStart,
        onBackground = Light.textBodyOnBackground,
    )

internal val DarkExtendedColorPalette =
    ExtendedColors(
        surfaceEnd = Dark.backgroundEnd,
        onBackgroundHeader = Dark.textHeaderOnBackground,
        tertiary = Dark.tertiaryButton,
        onTertiary = Dark.textTertiaryButton,
        callout = Dark.callout,
        onCallout = Dark.onCallout,
        circularProgressBarSmall = Dark.circularProgressBarSmall,
        circularProgressBarSmallDark = Dark.circularProgressBarSmallDark,
        circularProgressBarScreen = Dark.circularProgressBarScreen,
        linearProgressBarTrack = Dark.linearProgressBarTrack,
        linearProgressBarBackground = Dark.linearProgressBarBackground,
        restoringTopAppBarColor = Dark.restoringTopAppBarColor,
        chipIndex = Dark.textChipIndex,
        textCommon = Dark.textCommon,
        textMedium = Dark.textMedium,
        textDisabled = Dark.textDisabled,
        textFieldFrame = Dark.textFieldFrame,
        textFieldError = Dark.textFieldError,
        textFieldHint = Dark.textFieldHint,
        textDescription = Dark.textDescription,
        textPending = Dark.textProgress,
        layoutStroke = Dark.layoutStroke,
        overlay = Dark.overlay,
        highlight = Dark.highlight,
        disabledButtonTextColor = Dark.disabledButtonTextColor,
        disabledButtonColor = Dark.disabledButtonColor,
        reference = Dark.reference,
        buttonShadowColor = Dark.buttonShadowColor,
        screenTitleColor = Dark.screenTitleColor,
        aboutTextColor = Dark.aboutTextColor,
        welcomeAnimationColor = Dark.welcomeAnimationColor,
        complementaryColor = Dark.complementaryColor,
        dividerColor = Dark.dividerColor,
        darkDividerColor = Dark.darkDividerColor,
        tabTextColor = Dark.tabTextColor,
        panelBackgroundColor = Dark.panelBackgroundColor,
        radioButtonColor = Dark.radioButtonColor,
        radioButtonTextColor = Dark.radioButtonTextColor,
        historyBackgroundColor = Dark.historyBackgroundColor,
        historyRedColor = Dark.historyRedColor,
    )

internal val LightExtendedColorPalette =
    ExtendedColors(
        surfaceEnd = Light.backgroundEnd,
        onBackgroundHeader = Light.textHeaderOnBackground,
        tertiary = Light.tertiaryButton,
        onTertiary = Light.textTertiaryButton,
        callout = Light.callout,
        onCallout = Light.onCallout,
        circularProgressBarScreen = Light.circularProgressBarScreen,
        circularProgressBarSmall = Light.circularProgressBarSmall,
        circularProgressBarSmallDark = Light.circularProgressBarSmallDark,
        linearProgressBarTrack = Light.linearProgressBarTrack,
        linearProgressBarBackground = Light.linearProgressBarBackground,
        restoringTopAppBarColor = Light.restoringTopAppBarColor,
        chipIndex = Light.textChipIndex,
        textCommon = Light.textCommon,
        textMedium = Light.textMedium,
        textDisabled = Light.textDisabled,
        textFieldFrame = Light.textFieldFrame,
        textFieldError = Light.textFieldError,
        textFieldHint = Light.textFieldHint,
        textDescription = Light.textDescription,
        textPending = Light.textProgress,
        layoutStroke = Light.layoutStroke,
        overlay = Light.overlay,
        highlight = Light.highlight,
        disabledButtonTextColor = Light.disabledButtonTextColor,
        disabledButtonColor = Light.disabledButtonColor,
        reference = Light.reference,
        buttonShadowColor = Light.buttonShadowColor,
        screenTitleColor = Light.screenTitleColor,
        aboutTextColor = Light.aboutTextColor,
        welcomeAnimationColor = Light.welcomeAnimationColor,
        complementaryColor = Light.complementaryColor,
        dividerColor = Light.dividerColor,
        darkDividerColor = Light.darkDividerColor,
        tabTextColor = Light.tabTextColor,
        panelBackgroundColor = Light.panelBackgroundColor,
        radioButtonColor = Light.radioButtonColor,
        radioButtonTextColor = Light.radioButtonTextColor,
        historyBackgroundColor = Light.historyBackgroundColor,
        historyRedColor = Light.historyRedColor,
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalExtendedColors =
    staticCompositionLocalOf {
        ExtendedColors(
            surfaceEnd = Color.Unspecified,
            onBackgroundHeader = Color.Unspecified,
            tertiary = Color.Unspecified,
            onTertiary = Color.Unspecified,
            callout = Color.Unspecified,
            onCallout = Color.Unspecified,
            circularProgressBarScreen = Color.Unspecified,
            circularProgressBarSmall = Color.Unspecified,
            circularProgressBarSmallDark = Color.Unspecified,
            linearProgressBarTrack = Color.Unspecified,
            linearProgressBarBackground = Color.Unspecified,
            restoringTopAppBarColor = Color.Unspecified,
            chipIndex = Color.Unspecified,
            textCommon = Color.Unspecified,
            textMedium = Color.Unspecified,
            textDisabled = Color.Unspecified,
            textFieldHint = Color.Unspecified,
            textFieldError = Color.Unspecified,
            textFieldFrame = Color.Unspecified,
            textDescription = Color.Unspecified,
            textPending = Color.Unspecified,
            layoutStroke = Color.Unspecified,
            overlay = Color.Unspecified,
            highlight = Color.Unspecified,
            disabledButtonTextColor = Color.Unspecified,
            disabledButtonColor = Color.Unspecified,
            reference = Color.Unspecified,
            buttonShadowColor = Color.Unspecified,
            screenTitleColor = Color.Unspecified,
            aboutTextColor = Color.Unspecified,
            welcomeAnimationColor = Color.Unspecified,
            complementaryColor = Color.Unspecified,
            dividerColor = Color.Unspecified,
            darkDividerColor = Color.Unspecified,
            tabTextColor = Color.Unspecified,
            panelBackgroundColor = Color.Unspecified,
            radioButtonColor = Color.Unspecified,
            radioButtonTextColor = Color.Unspecified,
            historyBackgroundColor = Color.Unspecified,
            historyRedColor = Color.Unspecified,
        )
    }
