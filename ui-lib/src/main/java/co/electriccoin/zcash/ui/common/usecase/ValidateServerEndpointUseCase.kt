package co.electriccoin.zcash.ui.common.usecase

import android.app.Application
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateServerEndpointUseCase(
    private val application: Application,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(endpoint: LightWalletEndpoint) =
        walletRepository
            .synchronizer
            .filterNotNull()
            .first()
            .validateServerEndpoint(application, endpoint)
}
