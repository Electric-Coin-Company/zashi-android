package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.screen.error.ErrorBottomSheet
import co.electriccoin.zcash.ui.screen.error.ErrorDialog

class NavigateToErrorUseCase(
    private val navigationRouter: NavigationRouter,
) {
    private var args: ErrorArgs? = null

    operator fun invoke(args: ErrorArgs) {
        this.args = args
        when (args) {
            is ErrorArgs.ShieldingError -> navigationRouter.forward(ErrorDialog)
            is ErrorArgs.SyncError -> navigationRouter.forward(ErrorBottomSheet)
            is ErrorArgs.General -> navigationRouter.forward(ErrorDialog)
            is ErrorArgs.ShieldingGeneralError -> navigationRouter.forward(ErrorDialog)
            is ErrorArgs.SynchronizerTorInitError -> navigationRouter.forward(ErrorDialog)
        }
    }

    fun requireCurrentArgs() = args as ErrorArgs

    fun clear() {
        args = null
    }
}

sealed interface ErrorArgs {
    data class SyncError(
        val synchronizerError: SynchronizerError
    ) : ErrorArgs

    data class ShieldingError(
        val error: SubmitResult.Failure
    ) : ErrorArgs

    data class ShieldingGeneralError(
        val exception: Exception
    ) : ErrorArgs

    data class General(
        val exception: Exception
    ) : ErrorArgs

    data object SynchronizerTorInitError :ErrorArgs
}
