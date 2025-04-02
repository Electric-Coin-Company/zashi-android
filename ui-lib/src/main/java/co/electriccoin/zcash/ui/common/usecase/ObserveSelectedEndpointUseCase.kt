package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveSelectedEndpointUseCase(
    private val persistableWalletProvider: PersistableWalletProvider
) {
    operator fun invoke() =
        persistableWalletProvider.persistableWallet
            .map {
                it?.endpoint
            }
            .distinctUntilChanged()
}
