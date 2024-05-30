package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.internal.TopAppBarColors

@Immutable
data class ExtendedColors(
    val backgroundColor: Color,
    val gridColor: Color,
    val onBackgroundHeader: Color,
    val circularProgressBarSmall: Color,
    val circularProgressBarSmallDark: Color,
    val circularProgressBarScreen: Color,
    val linearProgressBarTrack: Color,
    val linearProgressBarBackground: Color,
    val textCommon: Color,
    val textDescription: Color,
    val textDisabled: Color,
    val textFieldHint: Color,
    val textFieldWarning: Color,
    val textFieldFrame: Color,
    val textDescriptionDark: Color,
    val layoutStroke: Color,
    val overlay: Color,
    val reference: Color,
    val disabledButtonColor: Color,
    val disabledButtonTextColor: Color,
    val welcomeAnimationColor: Color,
    val complementaryColor: Color,
    val dividerColor: Color,
    val darkDividerColor: Color,
    val panelBackgroundColor: Color,
    val cameraDisabledBackgroundColor: Color,
    val cameraDisabledFrameColor: Color,
    val radioButtonColor: Color,
    val radioButtonTextColor: Color,
    val historyBackgroundColor: Color,
    val historyRedColor: Color,
    val historySyncingColor: Color,
    val topAppBarColors: TopAppBarColors,
    val transparentTopAppBarColors: TopAppBarColors
)
