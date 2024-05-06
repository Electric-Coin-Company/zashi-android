package co.electriccoin.zcash.ui.design.util

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.spackle.Twig

/**
 * This operation performs calculation of the screen height.
 *
 * @return [ScreenHeight] a wrapper object of the calculated heights in density pixels.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun screenHeight(): ScreenHeight {
    val configuration = LocalConfiguration.current

    val statusBars = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues().calculateTopPadding()
    Twig.debug { "Screen height: Status bar height raw: $statusBars" }

    val navigationBars = WindowInsets.navigationBarsIgnoringVisibility.asPaddingValues().calculateBottomPadding()
    Twig.debug { "Screen height: Navigation bar height raw: $navigationBars" }

    val contentHeight = configuration.screenHeightDp.dp
    Twig.debug { "Screen height: Screen content height: $contentHeight" }

    val statusBarHeight =
        statusBars.run {
            if (value <= 0f) {
                24.dp
            } else {
                this
            }
        }
    Twig.debug { "Screen height: Status bar height: $statusBarHeight" }

    val navigationBarHeight =
        navigationBars.run {
            if (value <= 0f) {
                88.dp
            } else {
                this
            }
        }
    Twig.debug { "Screen height: Navigation bar height: $navigationBarHeight" }

    return ScreenHeight(
        contentHeight = contentHeight,
        systemStatusBarHeight = statusBarHeight,
        systemNavigationBarHeight = navigationBarHeight
    )
}

data class ScreenHeight(
    val contentHeight: Dp,
    val systemStatusBarHeight: Dp,
    val systemNavigationBarHeight: Dp
) {
    fun overallScreenHeight(): Dp {
        return (contentHeight + systemBarsHeight()).also {
            Twig.debug { "Screen height: Overall height: $it" }
        }
    }

    fun systemBarsHeight(): Dp {
        return (systemStatusBarHeight + systemNavigationBarHeight).also {
            Twig.debug { "Screen height: System bars height: $it" }
        }
    }
}
