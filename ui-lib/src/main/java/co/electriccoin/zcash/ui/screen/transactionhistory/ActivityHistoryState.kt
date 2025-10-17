package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource
import java.util.UUID

@Immutable
sealed interface ActivityHistoryState {
    val onBack: () -> Unit
    val filterButton: IconButtonState

    @Immutable
    data class Loading(
        override val onBack: () -> Unit,
        override val filterButton: IconButtonState
    ) : ActivityHistoryState

    @Immutable
    data class Empty(
        override val onBack: () -> Unit,
        override val filterButton: IconButtonState
    ) : ActivityHistoryState

    @Immutable
    data class Data(
        override val onBack: () -> Unit,
        override val filterButton: IconButtonState,
        val items: List<ActivityHistoryItem>,
        val filtersId: String = UUID.randomUUID().toString()
    ) : ActivityHistoryState
}

@Immutable
sealed interface ActivityHistoryItem {
    @Immutable
    data class Header(
        val title: StringResource,
        override val key: Any = UUID.randomUUID()
    ) : ActivityHistoryItem, Itemizable {
        override val contentType = "Transaction Header"
    }

    @Immutable
    data class Activity(val state: ActivityState) : ActivityHistoryItem
}
