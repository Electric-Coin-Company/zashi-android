package co.electriccoin.zcash.ui.common.usecase

import android.app.Application
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.type.ServerValidation
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class PersistEndpointUseCase(
    private val application: Application,
    private val walletRepository: WalletRepository,
    private val getPersistableWallet: GetPersistableWalletUseCase,
    private val synchronizerProvider: SynchronizerProvider,
) {
    @Throws(PersistEndpointException::class)
    suspend operator fun invoke(endpoint: LightWalletEndpoint) {
        val selected = getPersistableWallet().endpoint

        if (selected == endpoint) return

        when (val result = validateServerEndpoint(endpoint)) {
            ServerValidation.Valid -> {
                persistWallet(getPersistableWallet().copy(endpoint = endpoint))
            }

            is ServerValidation.InValid -> throw PersistEndpointException(result.reason.message)
            ServerValidation.Running -> throw PersistEndpointException(null)
        }
    }

    private suspend fun validateServerEndpoint(endpoint: LightWalletEndpoint) =
        synchronizerProvider.getSynchronizer()
            .validateServerEndpoint(application, endpoint)

    private fun persistWallet(persistableWallet: PersistableWallet) {
        walletRepository.persistWallet(persistableWallet)
    }
}

class PersistEndpointException(message: String?) : Exception(message)
