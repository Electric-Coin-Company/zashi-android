package co.electriccoin.zcash.ui.design.theme.internal

import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
import android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
import android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.Twig

@Immutable
data class Dimens(
    // Default spacings:
    val spacingXtiny: Dp,
    val spacingTiny: Dp,
    val spacingSmall: Dp,
    val spacingDefault: Dp,
    val spacingLarge: Dp,
    val spacingXlarge: Dp,
    val spacingHuge: Dp,

    // List of custom spacings:
)

private val defaultDimens = Dimens(
    spacingXtiny = 2.dp,
    spacingTiny = 4.dp,
    spacingSmall = 8.dp,
    spacingDefault = 16.dp,
    spacingLarge = 24.dp,
    spacingXlarge = 32.dp,
    spacingHuge = 64.dp,
)

private val normalDimens = defaultDimens

// We could also split large screens and provide separated values
private val largeDimens = defaultDimens

private val smallDimens = Dimens(
    spacingXtiny = 1.dp,
    spacingTiny = 2.dp,
    spacingSmall = 4.dp,
    spacingDefault = 8.dp,
    spacingLarge = 12.dp,
    spacingXlarge = 16.dp,
    spacingHuge = 32.dp,
)

private val xlargeDimens = Dimens(
    spacingXtiny = 4.dp,
    spacingTiny = 8.dp,
    spacingSmall = 16.dp,
    spacingDefault = 32.dp,
    spacingLarge = 48.dp,
    spacingXlarge = 64.dp,
    spacingHuge = 128.dp,
)

internal var LocalDimens = staticCompositionLocalOf { defaultDimens }

@Composable
internal fun ProvideDimens(content: @Composable () -> Unit,) {
    val screenLayoutBitMask = LocalConfiguration.current.screenLayout

    val resultDimens = when (screenLayoutBitMask and SCREENLAYOUT_SIZE_MASK) {
        SCREENLAYOUT_SIZE_SMALL -> {
            Twig.info { "Current device screen size: SMALL. Screen is approximately 320x426 dp at least." }
            smallDimens
        }
        SCREENLAYOUT_SIZE_NORMAL -> {
            Twig.info { "Current device screen size: NORMAL. Screen is approximately 320x470 dp at least." }
            normalDimens
        }
        SCREENLAYOUT_SIZE_LARGE -> {
            Twig.info { "Current device screen size: LARGE. Screen is approximately 480x640 dp at least." }
            largeDimens
        }
        SCREENLAYOUT_SIZE_XLARGE -> {
            Twig.info { "Current device screen size: XLARGE. Screen is approximately 720x960 dp at least." }
            xlargeDimens
        }
        else -> {
            Twig.info { "Current device screen size: UNDEFINED - using NORMAL size values." }
            normalDimens
        }
    }
    LocalDimens = staticCompositionLocalOf { resultDimens }

    // Moreover we could also use different values based on these metrics:
    // - landscape/portrait orientation modes
    // - long/normal screen aspect ratio (screen is wider/taller than normal)
    // - LRT/RTL screen set up
    // - rounded/normal screen shape

    CompositionLocalProvider(LocalDimens provides resultDimens, content = content)
}
