package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.TransactionFilter
import co.electriccoin.zcash.ui.common.repository.TransactionFilterRepository

class ApplyTransactionFiltersUseCase(
    private val transactionFilterRepository: TransactionFilterRepository,
    private val navigationRouter: NavigationRouter,
) {
    suspend operator fun invoke(
        filters: List<TransactionFilter>,
        hideBottomSheet: suspend () -> Unit
    ) {
        transactionFilterRepository.apply(filters)
        hideBottomSheet()
        navigationRouter.back()
    }
}
