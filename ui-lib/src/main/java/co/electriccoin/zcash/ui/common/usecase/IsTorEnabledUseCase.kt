package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class IsTorEnabledUseCase(
    private val synchronizerProvider: SynchronizerProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() = synchronizerProvider.synchronizer
        .mapLatest {
            it?.flags
        }
        .map {
            it?.isTorEnabled == true
        }
        .distinctUntilChanged()
}
