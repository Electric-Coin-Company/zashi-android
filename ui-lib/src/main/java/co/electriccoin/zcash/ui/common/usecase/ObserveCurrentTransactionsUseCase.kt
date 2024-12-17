package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionRepository

class ObserveCurrentTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke() = transactionRepository.currentTransactions
}
