package co.electriccoin.zcash.ui.screen.error

import androidx.navigation.NavDestination.Companion.hasRoute
import co.electriccoin.lightwallet.client.model.ResponseException
import co.electriccoin.lightwallet.client.model.UninitializedTorClientException
import co.electriccoin.zcash.ui.NavigationCommand
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SubmitResult
import co.electriccoin.zcash.ui.common.model.SynchronizerError
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.design.util.getCausesAsSequence
import co.electriccoin.zcash.ui.screen.home.HomeArgs

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

    fun navigateAutomaticallyToSyncError(message: HomeMessageData?) {
        if (message is HomeMessageData.Error && isSyncError(message.synchronizerError)) {
            navigationRouter.custom { entry ->
                if (entry?.destination?.hasRoute<HomeArgs>() == true) {
                    this.args = ErrorArgs.SyncError(message.synchronizerError)
                    NavigationCommand.Forward(listOf(SyncErrorArgs))
                } else {
                    null
                }
            }
        }
    }

    @Suppress("MagicNumber")
    private fun navigateToSyncError(args: ErrorArgs.SyncError, navigate: NavigationRouter.(Any) -> Unit) {
        if (isSyncError(args.synchronizerError)) {
            navigationRouter.navigate(SyncErrorArgs)
        } else {
            navigationRouter.navigate(ErrorBottomSheet)
        }
    }

    @Suppress("MagicNumber")
    private fun isSyncError(synchronizerError: SynchronizerError): Boolean =
        synchronizerError.cause
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
