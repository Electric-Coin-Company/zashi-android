@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import co.electriccoin.zcash.ui.screen.home.view.Home
import kotlinx.collections.immutable.ImmutableList
import kotlin.random.Random

@Composable
internal fun WrapHome(
    tabs: ImmutableList<TabItem>,
    forcePage: ForcePage?,
    onPageChange: (HomeScreenIndex) -> Unit,
    goBack: () -> Unit,
) {
    BackHandler {
        goBack()
    }

    Home(
        subScreens = tabs,
        forcePage = forcePage,
        onPageChange = onPageChange
    )
}

/**
 * Wrapper class used to pass forced pages index into the view layer
 */
class ForcePage(
    val currentPage: HomeScreenIndex = HomeScreenIndex.ACCOUNT,
) {
    // Ensures that every value of this class will be emitted in encapsulating stream API
    @Suppress("EqualsAlwaysReturnsTrueOrFalse")
    override fun equals(other: Any?) = false

    override fun hashCode() = Random.nextInt()
}

/**
 * Enum of the Home screen sub-screens
 */
enum class HomeScreenIndex {
    // WARN: Be careful when re-ordering these, as the ordinal number states for their order
    ACCOUNT,
    SEND,
    RECEIVE,
    BALANCES,;

    companion object {
        fun fromIndex(index: Int) = entries[index]
    }
}
