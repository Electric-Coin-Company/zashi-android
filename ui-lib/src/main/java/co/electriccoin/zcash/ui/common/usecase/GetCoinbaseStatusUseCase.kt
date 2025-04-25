package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.ConfigurationRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class GetCoinbaseStatusUseCase(
    private val configurationRepository: ConfigurationRepository,
) {
    fun observe() =
        configurationRepository
            .isCoinbaseAvailable
            .filterNotNull()
            .map { isAvailable ->
                if (isAvailable) {
                    Status.ENABLED
                } else {
                    Status.UNAVAILABLE
                }
            }.distinctUntilChanged()
}
