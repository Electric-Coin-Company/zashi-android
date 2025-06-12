package co.electriccoin.zcash.ui.common.usecase

import android.app.Application
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.type.ServerValidation
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class PersistEndpointUseCase(
    private val application: Application,
    private val walletRepository: WalletRepository,
    private val synchronizerProvider: SynchronizerProvider,
    private val persistableWalletProvider: PersistableWalletProvider
) {
    @Throws(PersistEndpointException::class)
    suspend operator fun invoke(endpoint: LightWalletEndpoint) {
        val selectedWallet = persistableWalletProvider.getPersistableWallet() ?: return
        val selectedEndpoint = selectedWallet.endpoint
        if (selectedEndpoint == endpoint) return
        when (val result = validateServerEndpoint(endpoint)) {
            ServerValidation.Valid -> persistWallet(selectedWallet.copy(endpoint = endpoint))
            is ServerValidation.InValid -> throw PersistEndpointException(result.reason.message)
            ServerValidation.Running -> throw PersistEndpointException(null)
        }
    }

    private suspend fun validateServerEndpoint(endpoint: LightWalletEndpoint) =
        synchronizerProvider
            .getSynchronizer()
            .validateServerEndpoint(application, endpoint)

    private fun persistWallet(persistableWallet: PersistableWallet) {
        walletRepository.persistWallet(persistableWallet)
    }
}

class PersistEndpointException(
    message: String?
) : Exception(message)
