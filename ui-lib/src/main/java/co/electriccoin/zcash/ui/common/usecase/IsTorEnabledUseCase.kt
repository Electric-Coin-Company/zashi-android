package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class IsTorEnabledUseCase(
    private val synchronizerProvider: SynchronizerProvider
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe() = synchronizerProvider.synchronizer
        .flatMapLatest {
            it?.flags ?: flowOf(null)
        }
        .map {
            it?.isTorEnabled == true
        }
        .distinctUntilChanged()
}
