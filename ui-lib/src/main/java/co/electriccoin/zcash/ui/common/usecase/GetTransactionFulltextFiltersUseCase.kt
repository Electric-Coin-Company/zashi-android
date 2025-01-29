package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository
import kotlinx.coroutines.flow.first

class GetTransactionFulltextFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository
) {
    suspend operator fun invoke() = observe().first()

    fun observe() = transactionFilterRepository.fulltextFilter
}
