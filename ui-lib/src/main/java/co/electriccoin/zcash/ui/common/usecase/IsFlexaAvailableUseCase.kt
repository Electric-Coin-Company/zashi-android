package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository

class IsFlexaAvailableUseCase(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke() = configurationRepository.isFlexaAvailable()

    fun observe() = configurationRepository.isFlexaAvailable
}
