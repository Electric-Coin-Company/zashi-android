@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.ExtendedColors

// TODO [#998]: Check and enhance screen dark mode
// TODO [#998]: https://github.com/Electric-Coin-Company/zashi-android/issues/998

internal object Dark {
    val backgroundColor = Color(0xFF231F20)
    val gridColor = Color(0xFF272727)

    val textHeaderOnBackground = Color(0xFFFFFFFF)
    val textBodyOnBackground = Color(0xFFFFFFFF)
    val textPrimaryButton = Color(0xFF000000)
    val textSecondaryButton = Color(0xFF000000)
    val textCommon = Color(0xFFFFFFFF)
    val textDisabled = Color(0xFFB7B7B7)
    val textFieldFrame = Color(0xFF231F20)
    val textFieldWarning = Color(0xFFF40202)
    val textFieldHint = Color(0xFFB7B7B7)
    val textDescription = Color(0xFF777777)
    val textDescriptionDark = Color(0xFF4D4D4D)

    val welcomeAnimationColor = Color(0xFF231F20)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFF000000)
    val layoutStroke = Color(0xFFFFFFFF)
    val panelBackgroundColor = Color(0xFFF6F6F6)
    val cameraDisabledBackgroundColor = Color(0xFF5E5C5C)
    val cameraDisabledFrameColor = Color(0xFFFFFFFF)

    val primaryButton = Color(0xFFFFFFFF)
    val secondaryButton = Color(0xFFFFFFFF)

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarSmallDark = textBodyOnBackground
    val circularProgressBarScreen = Color(0xFFFFFFFF)
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor

    val overlay = Color(0x22000000)

    val reference = Color(0xFFFFFFFF)

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = textFieldWarning
    val historySyncingColor = panelBackgroundColor

    val topAppBarColors = DarkTopAppBarColors()
    val transparentTopAppBarColors = TransparentTopAppBarColors()
}

internal object Light {
    val backgroundColor = Color(0xFFFFFFFF)
    val gridColor = Color(0xFFFBFBFB)

    val textHeaderOnBackground = Color(0xFF000000)
    val textBodyOnBackground = Color(0xFF000000)
    val textPrimaryButton = Color(0xFFFFFFFF)
    val textSecondaryButton = Color(0xFF000000)
    val textCommon = Color(0xFF000000)
    val textDisabled = Color(0xFFB7B7B7)
    val textFieldFrame = Color(0xFF231F20)
    val textFieldWarning = Color(0xFFF40202)
    val textFieldHint = Color(0xFFB7B7B7)
    val textDescription = Color(0xFF777777)
    val textDescriptionDark = Color(0xFF4D4D4D)

    val welcomeAnimationColor = Color(0xFF231F20)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFF000000)
    val layoutStroke = Color(0xFF000000)
    val panelBackgroundColor = Color(0xFFEBEBEB)
    val cameraDisabledBackgroundColor = Color(0xFF5E5C5C)
    val cameraDisabledFrameColor = Color(0xFFFFFFFF)

    val primaryButton = Color(0xFF000000)
    val secondaryButton = Color(0xFFFFFFFF)

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarScreen = Color(0xFF000000)
    val circularProgressBarSmallDark = textBodyOnBackground
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor

    val overlay = Color(0x22000000)

    val reference = Color(0xFF000000)

    val disabledButtonColor = Color(0xFFB7B7B7)
    val disabledButtonTextColor = Color(0xFFDDDDDD)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = textFieldWarning
    val historySyncingColor = panelBackgroundColor

    val topAppBarColors = LightTopAppBarColors()
    val transparentTopAppBarColors = TransparentTopAppBarColors()
}

internal val DarkColorPalette =
    darkColorScheme(
        primary = Dark.primaryButton,
        secondary = Dark.secondaryButton,
        onPrimary = Dark.textPrimaryButton,
        onSecondary = Dark.textSecondaryButton,
        surface = Dark.backgroundColor,
        onSurface = Dark.textBodyOnBackground,
        background = Dark.backgroundColor,
        onBackground = Dark.textBodyOnBackground,
    )

internal val LightColorPalette =
    lightColorScheme(
        primary = Light.primaryButton,
        secondary = Light.secondaryButton,
        onPrimary = Light.textPrimaryButton,
        onSecondary = Light.textSecondaryButton,
        surface = Light.backgroundColor,
        onSurface = Light.textBodyOnBackground,
        background = Light.backgroundColor,
        onBackground = Light.textBodyOnBackground,
    )

internal val DarkExtendedColorPalette =
    ExtendedColors(
        backgroundColor = Dark.backgroundColor,
        gridColor = Dark.gridColor,
        onBackgroundHeader = Dark.textHeaderOnBackground,
        circularProgressBarSmall = Dark.circularProgressBarSmall,
        circularProgressBarSmallDark = Dark.circularProgressBarSmallDark,
        circularProgressBarScreen = Dark.circularProgressBarScreen,
        linearProgressBarTrack = Dark.linearProgressBarTrack,
        linearProgressBarBackground = Dark.linearProgressBarBackground,
        textCommon = Dark.textCommon,
        textDisabled = Dark.textDisabled,
        textFieldFrame = Dark.textFieldFrame,
        textFieldWarning = Dark.textFieldWarning,
        textFieldHint = Dark.textFieldHint,
        textDescription = Dark.textDescription,
        textDescriptionDark = Dark.textDescriptionDark,
        layoutStroke = Dark.layoutStroke,
        overlay = Dark.overlay,
        disabledButtonTextColor = Dark.disabledButtonTextColor,
        disabledButtonColor = Dark.disabledButtonColor,
        reference = Dark.reference,
        welcomeAnimationColor = Dark.welcomeAnimationColor,
        complementaryColor = Dark.complementaryColor,
        dividerColor = Dark.dividerColor,
        darkDividerColor = Dark.darkDividerColor,
        panelBackgroundColor = Dark.panelBackgroundColor,
        cameraDisabledBackgroundColor = Dark.cameraDisabledBackgroundColor,
        cameraDisabledFrameColor = Dark.cameraDisabledFrameColor,
        radioButtonColor = Dark.radioButtonColor,
        radioButtonTextColor = Dark.radioButtonTextColor,
        historyBackgroundColor = Dark.historyBackgroundColor,
        historyRedColor = Dark.historyRedColor,
        historySyncingColor = Dark.historySyncingColor,
        topAppBarColors = Dark.topAppBarColors,
        transparentTopAppBarColors = Dark.transparentTopAppBarColors
    )

internal val LightExtendedColorPalette =
    ExtendedColors(
        backgroundColor = Light.backgroundColor,
        gridColor = Light.gridColor,
        onBackgroundHeader = Light.textHeaderOnBackground,
        circularProgressBarScreen = Light.circularProgressBarScreen,
        circularProgressBarSmall = Light.circularProgressBarSmall,
        circularProgressBarSmallDark = Light.circularProgressBarSmallDark,
        linearProgressBarTrack = Light.linearProgressBarTrack,
        linearProgressBarBackground = Light.linearProgressBarBackground,
        textCommon = Light.textCommon,
        textDisabled = Light.textDisabled,
        textFieldFrame = Light.textFieldFrame,
        textFieldWarning = Light.textFieldWarning,
        textFieldHint = Light.textFieldHint,
        textDescription = Light.textDescription,
        textDescriptionDark = Light.textDescriptionDark,
        layoutStroke = Light.layoutStroke,
        overlay = Light.overlay,
        disabledButtonTextColor = Light.disabledButtonTextColor,
        disabledButtonColor = Light.disabledButtonColor,
        reference = Light.reference,
        welcomeAnimationColor = Light.welcomeAnimationColor,
        complementaryColor = Light.complementaryColor,
        dividerColor = Light.dividerColor,
        darkDividerColor = Light.darkDividerColor,
        panelBackgroundColor = Light.panelBackgroundColor,
        cameraDisabledBackgroundColor = Light.cameraDisabledBackgroundColor,
        cameraDisabledFrameColor = Light.cameraDisabledFrameColor,
        radioButtonColor = Light.radioButtonColor,
        radioButtonTextColor = Light.radioButtonTextColor,
        historyBackgroundColor = Light.historyBackgroundColor,
        historyRedColor = Light.historyRedColor,
        historySyncingColor = Light.historySyncingColor,
        topAppBarColors = Light.topAppBarColors,
        transparentTopAppBarColors = Light.transparentTopAppBarColors
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalExtendedColors =
    staticCompositionLocalOf {
        ExtendedColors(
            backgroundColor = Color.Unspecified,
            gridColor = Color.Unspecified,
            onBackgroundHeader = Color.Unspecified,
            circularProgressBarScreen = Color.Unspecified,
            circularProgressBarSmall = Color.Unspecified,
            circularProgressBarSmallDark = Color.Unspecified,
            linearProgressBarTrack = Color.Unspecified,
            linearProgressBarBackground = Color.Unspecified,
            textCommon = Color.Unspecified,
            textDisabled = Color.Unspecified,
            textFieldHint = Color.Unspecified,
            textFieldWarning = Color.Unspecified,
            textFieldFrame = Color.Unspecified,
            textDescription = Color.Unspecified,
            textDescriptionDark = Color.Unspecified,
            layoutStroke = Color.Unspecified,
            overlay = Color.Unspecified,
            disabledButtonTextColor = Color.Unspecified,
            disabledButtonColor = Color.Unspecified,
            reference = Color.Unspecified,
            welcomeAnimationColor = Color.Unspecified,
            complementaryColor = Color.Unspecified,
            dividerColor = Color.Unspecified,
            darkDividerColor = Color.Unspecified,
            panelBackgroundColor = Color.Unspecified,
            cameraDisabledBackgroundColor = Color.Unspecified,
            cameraDisabledFrameColor = Color.Unspecified,
            radioButtonColor = Color.Unspecified,
            radioButtonTextColor = Color.Unspecified,
            historyBackgroundColor = Color.Unspecified,
            historyRedColor = Color.Unspecified,
            historySyncingColor = Color.Unspecified,
            topAppBarColors = DefaultTopAppBarColors(),
            transparentTopAppBarColors = DefaultTopAppBarColors(),
        )
    }
