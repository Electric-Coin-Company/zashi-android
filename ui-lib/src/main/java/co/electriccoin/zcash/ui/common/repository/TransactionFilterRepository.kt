package co.electriccoin.zcash.ui.common.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate

interface TransactionFilterRepository {
    val fulltextFilter: StateFlow<String?>

    val filters: StateFlow<List<TransactionFilter>>

    fun apply(filters: List<TransactionFilter>)

    fun applyFulltext(fulltext: String)

    fun clear()

    fun clearFulltext()
}

class TransactionFilterRepositoryImpl : TransactionFilterRepository {
    override val fulltextFilter = MutableStateFlow<String?>(null)

    override val filters = MutableStateFlow<List<TransactionFilter>>(emptyList())

    override fun apply(filters: List<TransactionFilter>) {
        this.filters.getAndUpdate { filters }
    }

    override fun applyFulltext(fulltext: String) {
        fulltextFilter.getAndUpdate { fulltext }
    }

    override fun clear() {
        filters.getAndUpdate { emptyList() }
    }

    override fun clearFulltext() {
        fulltextFilter.getAndUpdate { null }
    }
}

enum class TransactionFilter {
    SENT,
    RECEIVED,
    MEMOS,
    UNREAD,
    BOOKMARKED,
    NOTES,
    SWAP
}
