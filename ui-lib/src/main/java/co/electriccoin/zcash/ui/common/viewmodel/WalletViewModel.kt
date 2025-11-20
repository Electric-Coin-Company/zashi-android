package co.electriccoin.zcash.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.StateFlow

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
class WalletViewModel(
    synchronizerProvider: SynchronizerProvider,
    private val walletRepository: WalletRepository,
) : ViewModel() {
    val synchronizer = synchronizerProvider.synchronizer

    val secretState: StateFlow<SecretState> = walletRepository.secretState

    fun createNewWallet() {
        walletRepository.createNewWallet()
    }

    fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight
    ) {
        walletRepository.restoreWallet(network, seedPhrase, birthday)
    }
}

/**
 * Represents the state of the wallet secret.
 */
enum class SecretState {
    LOADING,
    NONE,
    READY
}

/**
 * This constant sets the default limitation on the length of the stack trace in the [SynchronizerError]
 */
const val STACKTRACE_LIMIT = 250

// TODO [#529]: Localize Synchronizer Errors
// TODO [#529]: https://github.com/Electric-Coin-Company/zashi-android/issues/529

/**
 * Represents all kind of Synchronizer errors
 */
sealed class SynchronizerError {
    abstract fun getCauseMessage(): String?

    abstract fun getStackTrace(limit: Int? = STACKTRACE_LIMIT): String?

    internal fun Throwable.stackTraceFullString() = stackTraceToString()

    internal fun Throwable.stackTraceToLimitedString(limit: Int) =
        if (stackTraceToString().isNotEmpty()) {
            stackTraceToString().substring(0..(stackTraceToString().length - 1).coerceAtMost(limit))
        } else {
            null
        }

    class Critical(
        val error: Throwable?
    ) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Processor(
        val error: Throwable?
    ) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Submission(
        val error: Throwable?
    ) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Setup(
        val error: Throwable?
    ) : SynchronizerError() {
        override fun getCauseMessage(): String? = error?.message

        override fun getStackTrace(limit: Int?): String? =
            if (limit != null) {
                error?.stackTraceToLimitedString(limit)
            } else {
                error?.stackTraceFullString()
            }
    }

    class Chain(
        val x: BlockHeight,
        val y: BlockHeight
    ) : SynchronizerError() {
        override fun getCauseMessage(): String = "$x, $y"

        override fun getStackTrace(limit: Int?): String? = null
    }
}
