package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.first

class GetExchangeRateUseCase(
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend operator fun invoke() = exchangeRateRepository.state.first()
}
