package co.electriccoin.zcash.ui.common.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

interface TransactionFilterRepository {
    val onFilterChanged: Flow<Unit>

    val fulltextFilter: StateFlow<String?>

    val filters: StateFlow<List<TransactionFilter>>

    fun apply(filters: List<TransactionFilter>)

    fun applyFulltext(fulltext: String)

    fun clear()

    fun clearFulltext()
}

class TransactionFilterRepositoryImpl : TransactionFilterRepository {
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val onFilterChanged = MutableSharedFlow<Unit>()

    override val fulltextFilter = MutableStateFlow<String?>(null)

    override val filters = MutableStateFlow<List<TransactionFilter>>(emptyList())

    override fun apply(filters: List<TransactionFilter>) {
        val old = this.filters.getAndUpdate { filters }
        if (old != filters) {
            scope.launch {
                onFilterChanged.emit(Unit)
            }
        }
    }

    override fun applyFulltext(fulltext: String) {
        val old = fulltextFilter.getAndUpdate { fulltext }
        if (old != fulltext) {
            scope.launch {
                onFilterChanged.emit(Unit)
            }
        }
    }

    override fun clear() {
        val old = filters.getAndUpdate { emptyList() }
        if (old != emptyList<TransactionFilter>()) {
            scope.launch {
                onFilterChanged.emit(Unit)
            }
        }
    }

    override fun clearFulltext() {
        val old = fulltextFilter.getAndUpdate { null }
        if (old != null) {
            scope.launch {
                onFilterChanged.emit(Unit)
            }
        }
    }
}

enum class TransactionFilter {
    SENT,
    RECEIVED,
    MEMOS,
    UNREAD,
    BOOKMARKED,
    NOTES
}
