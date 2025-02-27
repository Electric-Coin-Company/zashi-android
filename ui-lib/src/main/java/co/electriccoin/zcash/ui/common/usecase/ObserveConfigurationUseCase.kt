package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository

class ObserveConfigurationUseCase(
    private val configurationRepository: ConfigurationRepository
) {
    operator fun invoke() = configurationRepository.configurationFlow
}
