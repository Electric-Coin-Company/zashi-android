package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.BlockHeight
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.provider.IsKeepScreenOnDuringRestoreProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider

class ConfirmResyncUseCase(
    private val synchronizerProvider: SynchronizerProvider,
    private val isKeepScreenOnDuringRestoreProvider: IsKeepScreenOnDuringRestoreProvider,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke(blockHeight: BlockHeight) {
        val synchronizer = synchronizerProvider.getSynchronizer()
        synchronizer.rewindToNearestHeight(blockHeight)
        isKeepScreenOnDuringRestoreProvider.clear()
        navigationRouter.backToRoot()
    }
}
