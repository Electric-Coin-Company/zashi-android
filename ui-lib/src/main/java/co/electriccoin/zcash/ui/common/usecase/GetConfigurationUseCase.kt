package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository

class GetConfigurationUseCase(
    private val configurationRepository: ConfigurationRepository
) {
    fun observe() = configurationRepository.configurationFlow
}
