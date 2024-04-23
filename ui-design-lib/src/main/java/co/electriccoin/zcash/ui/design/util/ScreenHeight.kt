package co.electriccoin.zcash.ui.design.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.Twig
import kotlin.math.roundToInt

/**
 * This operation performs calculation of the screen height together with remembering its result for a further calls.
 *
 * @param cacheKey The cache defining key. Use a different one for recalculation.
 *
 * @return Wrapper object of the calculated heights in density pixels.
 */
@Composable
fun screenHeight(cacheKey: Any = true): ScreenHeight {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val statusBars = WindowInsets.statusBars
    val navigationBars = WindowInsets.navigationBars

    val cachedResult =
        remember(cacheKey) {
            val contentHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
            Twig.debug { "Screen content height in pixels: $contentHeightPx" }

            // TODO [#1382]: Analyse zero status and navigation bars height
            // TODO [#1382]: https://github.com/Electric-Coin-Company/zashi-android/issues/1382
            val statusBarHeight =
                statusBars.getTop(density).dp.run {
                    if (value <= 0f) {
                        48.dp
                    } else {
                        this
                    }
                }
            Twig.debug { "Status bar height: $statusBarHeight" }

            val navigationBarHeight =
                navigationBars.getBottom(density).dp.run {
                    if (value <= 0f) {
                        88.dp
                    } else {
                        this
                    }
                }
            Twig.debug { "Navigation bar height: $navigationBarHeight" }

            val contentHeight = (contentHeightPx / density.density.roundToInt()).dp
            Twig.debug { "Screen content height in dps: $contentHeight" }

            ScreenHeight(
                contentHeight = contentHeight,
                systemStatusBarHeight = statusBarHeight,
                systemNavigationBarHeight = navigationBarHeight,
            )
        }
    Twig.debug { "Screen total height: $cachedResult" }

    return cachedResult
}

data class ScreenHeight(
    val contentHeight: Dp,
    val systemStatusBarHeight: Dp,
    val systemNavigationBarHeight: Dp
) {
    fun overallScreenHeight(): Dp {
        return (contentHeight + systemBarsHeight()).also {
            Twig.debug { "Screen overall height: $it" }
        }
    }

    fun systemBarsHeight(): Dp {
        return (systemStatusBarHeight + systemNavigationBarHeight).also {
            Twig.debug { "System bars height: $this" }
        }
    }
}
