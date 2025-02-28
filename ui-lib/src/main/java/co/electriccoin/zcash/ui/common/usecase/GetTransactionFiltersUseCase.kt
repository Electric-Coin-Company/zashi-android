package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository

class GetTransactionFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository
) {
    operator fun invoke() = transactionFilterRepository.filters.value

    fun observe() = transactionFilterRepository.filters
}
