package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.type.ServerValidation
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

class PersistEndpointUseCase(
    private val validateServerEndpoint: ValidateServerEndpointUseCase,
    private val closeSynchronizer: CloseSynchronizerUseCase,
    private val persistWallet: PersistWalletUseCase,
    private val getPersistableWallet: GetPersistableWalletUseCase,
) {
    @Throws(PersistEndpointException::class)
    suspend operator fun invoke(endpoint: LightWalletEndpoint?) {
        val selected = getPersistableWallet().endpoint

        if (endpoint == null || selected == endpoint) {
            return
        }

        when (val result = validateServerEndpoint(endpoint)) {
            ServerValidation.Valid -> {
                closeSynchronizer()
                persistWallet(getPersistableWallet().copy(endpoint = endpoint))
            }
            is ServerValidation.InValid -> throw PersistEndpointException(result.reason.message)
            ServerValidation.Running -> throw PersistEndpointException(null)
        }
    }
}

class PersistEndpointException(message: String?) : Exception(message)
