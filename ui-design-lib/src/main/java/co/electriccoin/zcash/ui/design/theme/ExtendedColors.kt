package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.internal.ButtonColors
import co.electriccoin.zcash.ui.design.theme.internal.TopAppBarColors

@Immutable
data class ExtendedColors(
    val primaryColor: Color,
    val secondaryColor: Color,
    val backgroundColor: Color,
    val gridColor: Color,
    val circularProgressBarSmall: Color,
    val circularProgressBarSmallDark: Color,
    val circularProgressBarScreen: Color,
    val linearProgressBarTrack: Color,
    val linearProgressBarBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDescription: Color,
    val textDisabled: Color,
    val textFieldHint: Color,
    val textFieldWarning: Color,
    val textFieldFrame: Color,
    val textDescriptionDark: Color,
    val layoutStroke: Color,
    val layoutStrokeSecondary: Color,
    val overlay: Color,
    val overlayProgressBar: Color,
    val reference: Color,
    val primaryButtonColors: ButtonColors,
    val secondaryButtonColors: ButtonColors,
    val welcomeAnimationColor: Color,
    val complementaryColor: Color,
    val primaryDividerColor: Color,
    val secondaryDividerColor: Color,
    val tertiaryDividerColor: Color,
    val panelBackgroundColor: Color,
    val panelBackgroundColorActive: Color,
    val cameraDisabledBackgroundColor: Color,
    val cameraDisabledFrameColor: Color,
    val historyBackgroundColor: Color,
    val historyRedColor: Color,
    val historySyncingColor: Color,
    val topAppBarColors: TopAppBarColors,
    val transparentTopAppBarColors: TopAppBarColors,
)
