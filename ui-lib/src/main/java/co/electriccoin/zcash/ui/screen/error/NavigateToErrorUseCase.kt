package co.electriccoin.zcash.ui.screen.error

import co.electriccoin.lightwallet.client.model.ResponseException
import co.electriccoin.lightwallet.client.model.UninitializedTorClientException
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.SynchronizerError
import co.electriccoin.zcash.ui.design.util.getCausesAsSequence

class NavigateToErrorUseCase(
    private val navigationRouter: NavigationRouter,
) {
    private var args: ErrorArgs? = null

    operator fun invoke(args: ErrorArgs, navigate: NavigationRouter.(Any) -> Unit = { forward(it) }) {
        this.args = args
        when (args) {
            is ErrorArgs.ShieldingError -> navigationRouter.navigate(ErrorDialog)
            is ErrorArgs.SyncError -> navigateToSyncError(args, navigate)
            is ErrorArgs.General -> navigationRouter.navigate(ErrorDialog)
            is ErrorArgs.ShieldingGeneralError -> navigationRouter.navigate(ErrorDialog)
            is ErrorArgs.SynchronizerTorInitError -> navigationRouter.navigate(ErrorDialog)
        }
    }

    @Suppress("MagicNumber")
    private fun navigateToSyncError(args: ErrorArgs.SyncError, navigate: NavigationRouter.(Any) -> Unit) {
        val showSyncError =
            args.synchronizerError.cause
                ?.getCausesAsSequence()
                .orEmpty()
                .any { e ->
                    when {
                        e is ResponseException && e.code in 500..599 -> true

                        e is ResponseException &&
                            e.code == 3200 &&
                            (500..599)
                                .any { e.message.orEmpty().contains("$it", true) }
                        -> true

                        e is ResponseException &&
                            e.code == 3200 &&
                            e.message.orEmpty().contains("Tonic error: transport error", true)
                        -> true

                        e is UninitializedTorClientException -> true
                        else -> false
                    }
                }

        if (showSyncError) {
            navigationRouter.navigate(SyncErrorArgs)
        } else {
            navigationRouter.navigate(ErrorBottomSheet)
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
        val error: SubmitResult
    ) : ErrorArgs

    data class ShieldingGeneralError(
        val exception: Exception
    ) : ErrorArgs

    data class General(
        val exception: Exception
    ) : ErrorArgs

    data object SynchronizerTorInitError : ErrorArgs
}
