package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository

class ApplyTransactionFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository,
    private val navigationRouter: NavigationRouter,
) {
    operator fun invoke(filters: List<TransactionFilter>) {
        transactionFilterRepository.apply(filters)
        navigationRouter.back()
    }
}
