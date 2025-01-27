package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetCurrentTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke() = transactionRepository.currentTransactions.filterNotNull().first()

    fun observe() = transactionRepository.currentTransactions
}
