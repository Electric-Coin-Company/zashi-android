package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository

class ResetTransactionFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository,
) {
    operator fun invoke() {
        transactionFilterRepository.clear()
        transactionFilterRepository.clearFulltext()
    }
}
