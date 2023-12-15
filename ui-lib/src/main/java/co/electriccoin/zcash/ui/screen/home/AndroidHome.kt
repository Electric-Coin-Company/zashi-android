@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import co.electriccoin.zcash.ui.screen.home.view.Home
import kotlinx.collections.immutable.ImmutableList
import kotlin.random.Random

@Composable
internal fun WrapHome(
    tabs: ImmutableList<TabItem>,
    forcePage: ForcePage?,
    onPageChange: (Int) -> Unit,
    goBack: () -> Unit,
) {
    val subScreens = rememberSaveable { tabs }

    BackHandler {
        goBack()
    }

    Home(
        subScreens = subScreens,
        forcePage = forcePage,
        onPageChange = onPageChange
    )
}

/**
 * Wrapper class used to pass forced pages index into the view layer
 */
class ForcePage(
    val currentPageIndex: Int = 0,
) {
    // Ensures that every value of this class will be emitted in encapsulating stream API
    @Suppress("EqualsAlwaysReturnsTrueOrFalse")
    override fun equals(other: Any?) = false

    override fun hashCode() = Random.nextInt()
}
