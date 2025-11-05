package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider

class ValidateAddressUseCase(
    private val synchronizerProvider: SynchronizerProvider
) {
    suspend operator fun invoke(address: String) = synchronizerProvider.getSynchronizer().validateAddress(address)
}
