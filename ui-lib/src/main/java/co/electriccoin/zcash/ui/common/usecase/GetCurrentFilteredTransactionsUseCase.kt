package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

class GetCurrentFilteredTransactionsUseCase(
    private val transactionRepository: TransactionRepository,
    private val transactionFilterRepository: TransactionFilterRepository
) {
    suspend operator fun invoke() = observe().filterNotNull().first()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        combine(
            transactionRepository.currentTransactions,
            transactionFilterRepository.filters,
            transactionFilterRepository.fulltextFilter
        ) { transactions, filters, fulltextFilter ->
            Triple(transactions, filters, fulltextFilter)
        }.mapLatest { (transactions, filters, _) ->
            transactions
                ?.filter {
                    if (filters.isEmpty()) {
                        return@filter true
                    }

                    if (filters.contains(TransactionFilter.SENT) &&
                        it.overview.isSentTransaction &&
                        !it.overview.isShielding
                    ) {
                        return@filter true
                    }

                    if (filters.contains(TransactionFilter.RECEIVED) &&
                        !it.overview.isSentTransaction &&
                        !it.overview.isShielding
                    ) {
                        return@filter true
                    }

                    false
                }
        }
}
