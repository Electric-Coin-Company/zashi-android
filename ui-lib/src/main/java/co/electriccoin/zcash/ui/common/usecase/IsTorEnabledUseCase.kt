package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.PersistableWalletTorProvider
import co.electriccoin.zcash.ui.common.provider.TorState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

class IsTorEnabledUseCase(
    private val persistableWalletTorProvider: PersistableWalletTorProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() =
        persistableWalletTorProvider
            .observe()
            .mapLatest { it == TorState.EXPLICITLY_ENABLED }
            .distinctUntilChanged()
}
