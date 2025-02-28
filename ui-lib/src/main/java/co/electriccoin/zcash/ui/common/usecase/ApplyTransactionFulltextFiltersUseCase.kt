package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository

class ApplyTransactionFulltextFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository
) {
    operator fun invoke(fullTextFilter: String) = transactionFilterRepository.applyFulltext(fullTextFilter)
}
