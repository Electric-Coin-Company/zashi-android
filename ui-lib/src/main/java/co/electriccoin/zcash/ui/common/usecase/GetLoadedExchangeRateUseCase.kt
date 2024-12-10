package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.flow.first

class GetLoadedExchangeRateUseCase(
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend operator fun invoke() =
        exchangeRateRepository.state.first {
            when (it) {
                is ExchangeRateState.Data -> it.isLoading
                is ExchangeRateState.OptIn -> true
                ExchangeRateState.OptedOut -> true
            }
        }
}
