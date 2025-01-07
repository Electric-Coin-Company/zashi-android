package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository

class IsCoinbaseAvailableUseCase(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke() = configurationRepository.isCoinbaseAvailable()

    fun observe() = configurationRepository.isCoinbaseAvailable
}
