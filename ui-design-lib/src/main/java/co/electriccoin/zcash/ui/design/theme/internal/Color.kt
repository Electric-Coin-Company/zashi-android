@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.ExtendedColors

internal object Dark {
    val primaryColor = Color(0xFF231F20)
    val secondaryColor = Color(0xFFFFFFFF)

    val backgroundColor = primaryColor
    val gridColor = Color(0xFF272727)

    val textPrimary = secondaryColor
    val textSecondary = primaryColor
    val textDisabled = Color(0xFFB7B7B7)
    val textFieldFrame = Color(0xFFFFFFFF)
    val textFieldWarning = Color(0xFFF40202)
    val textFieldHint = Color(0xFFB7B7B7)
    val textDescription = Color(0xFF777777)
    val textDescriptionDark = Color(0xFF4D4D4D)

    val welcomeAnimationColor = Color(0xFF181716)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFFFFFFFF)
    val layoutStroke = Color(0xFFFFFFFF)
    val layoutStrokeSecondary = Color(0xFFDDDDDD)
    val panelBackgroundColor = Color(0xFFF6F6F6)
    val cameraDisabledBackgroundColor = Color(0xFF5E5C5C)
    val cameraDisabledFrameColor = Color(0xFFFFFFFF)

    val primaryButtonColors = DarkPrimaryButtonColors()
    val secondaryButtonColors = DarkSecondaryButtonColors()

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarSmallDark = textPrimary
    val circularProgressBarScreen = Color(0xFFFFFFFF)
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor

    val overlay = Color(0x22000000)
    val reference = Color(0xFFFFFFFF)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = textFieldWarning
    val historySyncingColor = panelBackgroundColor

    val topAppBarColors = DarkTopAppBarColors()
    val transparentTopAppBarColors = TransparentTopAppBarColors()
}

internal object Light {
    val primaryColor = Color(0xFFFFFFFF)
    val secondaryColor = Color(0xFF000000)

    val backgroundColor = primaryColor
    val gridColor = Color(0xFFFBFBFB)

    val textPrimary = secondaryColor
    val textSecondary = primaryColor
    val textDisabled = Color(0xFFB7B7B7)
    val textFieldFrame = Color(0xFF000000)
    val textFieldWarning = Color(0xFFF40202)
    val textFieldHint = Color(0xFFB7B7B7)
    val textDescription = Color(0xFF777777)
    val textDescriptionDark = Color(0xFF4D4D4D)

    val welcomeAnimationColor = Color(0xFF231F20)
    val complementaryColor = Color(0xFFF4B728)
    val dividerColor = Color(0xFFDDDDDD)
    val darkDividerColor = Color(0xFF000000)
    val layoutStroke = Color(0xFF000000)
    val layoutStrokeSecondary = Color(0xFFDDDDDD)
    val panelBackgroundColor = Color(0xFFEBEBEB)
    val cameraDisabledBackgroundColor = Color(0xFF5E5C5C)
    val cameraDisabledFrameColor = Color(0xFFFFFFFF)

    val primaryButtonColors = LightPrimaryButtonColors()
    val secondaryButtonColors = LightSecondaryButtonColors()

    val radioButtonColor = Color(0xFF070707)
    val radioButtonTextColor = Color(0xFF4E4E4E)

    val circularProgressBarSmall = Color(0xFF8B8A8A)
    val circularProgressBarScreen = Color(0xFF000000)
    val circularProgressBarSmallDark = textPrimary
    val linearProgressBarTrack = Color(0xFFD9D9D9)
    val linearProgressBarBackground = complementaryColor

    val overlay = Color(0x22000000)

    val reference = Color(0xFF000000)

    val historyBackgroundColor = Color(0xFFF6F6F6)
    val historyRedColor = textFieldWarning
    val historySyncingColor = panelBackgroundColor

    val topAppBarColors = LightTopAppBarColors()
    val transparentTopAppBarColors = TransparentTopAppBarColors()
}

internal val DarkColorPalette =
    darkColorScheme(
        // Our colors intentionally use a different naming than the ones from MaterialTheme
        primary = Dark.textPrimary,
        secondary = Dark.secondaryColor,
        onPrimary = Dark.textPrimary,
        onSecondary = Dark.textSecondary,
        surface = Dark.backgroundColor,
        onSurface = Dark.textPrimary,
        background = Dark.backgroundColor,
        onBackground = Dark.textPrimary,
    )

internal val LightColorPalette =
    lightColorScheme(
        // Our colors intentionally use a different naming than the ones from MaterialTheme
        primary = Light.textPrimary,
        secondary = Light.secondaryColor,
        onPrimary = Light.textPrimary,
        onSecondary = Light.textSecondary,
        surface = Light.backgroundColor,
        onSurface = Light.textPrimary,
        background = Light.backgroundColor,
        onBackground = Light.textPrimary,
    )

internal val DarkExtendedColorPalette =
    ExtendedColors(
        primaryColor = Dark.primaryColor,
        secondaryColor = Dark.secondaryColor,
        backgroundColor = Dark.backgroundColor,
        gridColor = Dark.gridColor,
        circularProgressBarSmall = Dark.circularProgressBarSmall,
        circularProgressBarSmallDark = Dark.circularProgressBarSmallDark,
        circularProgressBarScreen = Dark.circularProgressBarScreen,
        linearProgressBarTrack = Dark.linearProgressBarTrack,
        linearProgressBarBackground = Dark.linearProgressBarBackground,
        textPrimary = Dark.textPrimary,
        textSecondary = Dark.textSecondary,
        textDisabled = Dark.textDisabled,
        textFieldFrame = Dark.textFieldFrame,
        textFieldWarning = Dark.textFieldWarning,
        textFieldHint = Dark.textFieldHint,
        textDescription = Dark.textDescription,
        textDescriptionDark = Dark.textDescriptionDark,
        layoutStroke = Dark.layoutStroke,
        layoutStrokeSecondary = Dark.layoutStrokeSecondary,
        overlay = Dark.overlay,
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
        transparentTopAppBarColors = Dark.transparentTopAppBarColors,
        primaryButtonColors = Dark.primaryButtonColors,
        secondaryButtonColors = Dark.secondaryButtonColors,
    )

internal val LightExtendedColorPalette =
    ExtendedColors(
        primaryColor = Light.primaryColor,
        secondaryColor = Light.secondaryColor,
        backgroundColor = Light.backgroundColor,
        gridColor = Light.gridColor,
        circularProgressBarScreen = Light.circularProgressBarScreen,
        circularProgressBarSmall = Light.circularProgressBarSmall,
        circularProgressBarSmallDark = Light.circularProgressBarSmallDark,
        linearProgressBarTrack = Light.linearProgressBarTrack,
        linearProgressBarBackground = Light.linearProgressBarBackground,
        textPrimary = Light.textPrimary,
        textSecondary = Light.textSecondary,
        textDisabled = Light.textDisabled,
        textFieldFrame = Light.textFieldFrame,
        textFieldWarning = Light.textFieldWarning,
        textFieldHint = Light.textFieldHint,
        textDescription = Light.textDescription,
        textDescriptionDark = Light.textDescriptionDark,
        layoutStroke = Light.layoutStroke,
        layoutStrokeSecondary = Light.layoutStrokeSecondary,
        overlay = Light.overlay,
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
        transparentTopAppBarColors = Light.transparentTopAppBarColors,
        primaryButtonColors = Light.primaryButtonColors,
        secondaryButtonColors = Light.secondaryButtonColors,
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalExtendedColors =
    staticCompositionLocalOf {
        ExtendedColors(
            primaryColor = Color.Unspecified,
            secondaryColor = Color.Unspecified,
            backgroundColor = Color.Unspecified,
            gridColor = Color.Unspecified,
            circularProgressBarScreen = Color.Unspecified,
            circularProgressBarSmall = Color.Unspecified,
            circularProgressBarSmallDark = Color.Unspecified,
            linearProgressBarTrack = Color.Unspecified,
            linearProgressBarBackground = Color.Unspecified,
            textPrimary = Color.Unspecified,
            textSecondary = Color.Unspecified,
            textDisabled = Color.Unspecified,
            textFieldHint = Color.Unspecified,
            textFieldWarning = Color.Unspecified,
            textFieldFrame = Color.Unspecified,
            textDescription = Color.Unspecified,
            textDescriptionDark = Color.Unspecified,
            layoutStroke = Color.Unspecified,
            layoutStrokeSecondary = Color.Unspecified,
            overlay = Color.Unspecified,
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
            primaryButtonColors = DefaultButtonColors(),
            secondaryButtonColors = DefaultButtonColors(),
        )
    }
