package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    // Default spacings:
    val spacingNone: Dp,
    val spacingXtiny: Dp,
    val spacingTiny: Dp,
    val spacingSmall: Dp,
    val spacingDefault: Dp,
    val spacingLarge: Dp,
    val spacingXlarge: Dp,
    val spacingHuge: Dp,

    // List of custom spacings:
    val shadowOffsetX: Dp,
    val shadowOffsetY: Dp,
    val shadowSpread: Dp,
    val defaultButtonWidth: Dp,
    val defaultButtonHeight: Dp,
)

private val defaultDimens = Dimens(
    spacingNone = 0.dp,
    spacingXtiny = 2.dp,
    spacingTiny = 4.dp,
    spacingSmall = 8.dp,
    spacingDefault = 16.dp,
    spacingLarge = 24.dp,
    spacingXlarge = 32.dp,
    spacingHuge = 64.dp,
    shadowOffsetX = 20.dp,
    shadowOffsetY = 20.dp,
    shadowSpread = 10.dp,
    defaultButtonWidth = 230.dp,
    defaultButtonHeight = 50.dp,
)

private val normalDimens = defaultDimens

internal var LocalDimens = staticCompositionLocalOf { defaultDimens }

/**
 * This is a convenience way on how to provide device specification based spacings. We use Configuration from Compose
 * package for this purpose.
 *
 * For now we use just "normal" sized spacings for phone devices. Here is an example how we could split spacings to
 * more groups based on screen size:
 *
 *     val screenLayoutBitMask = LocalConfiguration.current.screenLayout
 *     val resultDimens = when (screenLayoutBitMask and SCREENLAYOUT_SIZE_MASK) {
 *         SCREENLAYOUT_SIZE_SMALL -> {
 *             Twig.info { "Current device screen size: SMALL. Screen is approximately 320x426 dp at least." }
 *             smallDimens
 *         }
 *         SCREENLAYOUT_SIZE_NORMAL -> {
 *             Twig.info { "Current device screen size: NORMAL. Screen is approximately 320x470 dp at least." }
 *             normalDimens
 *         }
 *         SCREENLAYOUT_SIZE_LARGE -> {
 *             Twig.info { "Current device screen size: LARGE. Screen is approximately 480x640 dp at least." }
 *             largeDimens
 *         }
 *         SCREENLAYOUT_SIZE_XLARGE -> {
 *             Twig.info { "Current device screen size: XLARGE. Screen is approximately 720x960 dp at least." }
 *             xlargeDimens
 *         }
 *         else -> {
 *             Twig.info { "Current device screen size: UNDEFINED - using NORMAL size values." }
 *             normalDimens
 *         }
 *     }
 *     LocalDimens = staticCompositionLocalOf { resultDimens }
 *
 *
 * Alternatively we could also use different values based on these metrics:
 *  - landscape/portrait orientation modes
 *  - long/normal screen aspect ratio (screen is wider/taller than normal)
 *  - LRT/RTL screen set up
 *  - rounded/normal screen shape
 */
@Composable
internal fun ProvideDimens(content: @Composable () -> Unit,) {
    val resultDimens = normalDimens
    CompositionLocalProvider(LocalDimens provides resultDimens, content = content)
}
