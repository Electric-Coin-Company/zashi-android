package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.Normalizer
import java.text.Normalizer.normalize

class GetTransactionFulltextFiltersUseCase(
    private val observeSynchronizer: ObserveSynchronizerUseCase,
    private val transactionFilterRepository: TransactionFilterRepository
) {
    suspend operator fun invoke(txId: FirstClassByteArray) = observe(txId).first()

    suspend fun observe(txId: FirstClassByteArray) =
        combine(
            transactionFilterRepository.fulltextFilter.map { it.orEmpty() },
            observeSynchronizer().filterNotNull()
        ) { fulltextFilter, synchronizer ->
            filterByMemoSubstring(fulltextFilter, txId, synchronizer)
        }

    private suspend fun filterByMemoSubstring(
        query: String,
        txId: FirstClassByteArray,
        synchronizer: Synchronizer
    ): Boolean {
        val normalizedQuery = query.normalizeQuery()
        return synchronizer.getTransactionsByMemoSubstring(normalizedQuery)
            .filter { it.contains(txId) }
            .count() > 0
    }

    private fun String.normalizeQuery() =
        trim().run {
            normalize(this, Normalizer.Form.NFKD)
        }
}
