package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.SwapRepository

class RecoverFundsHotfixUseCase(
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
    private val swapRepository: SwapRepository,
) {
    suspend operator fun invoke(address: String) {
        val validation = synchronizerProvider.getSynchronizer().validateAddress(address)

        when (validation) {
            AddressType.Shielded,
            AddressType.Unified -> {
                // do nothing
            }
            AddressType.Tex,
            AddressType.Transparent -> fixEphemeralOrTransparentStuckFunds(address)
            is AddressType.Invalid -> {
                val recipient = swapRepository.getSwapStatus(depositAddress = address).status?.quote?.recipient

                if (recipient != null) {
                    fixEphemeralOrTransparentStuckFunds(recipient)
                }
            }
        }
    }

    private suspend fun fixEphemeralOrTransparentStuckFunds(address: String) {
        navigationRouter.back()
    }
}
