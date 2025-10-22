package co.electriccoin.zcash.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.FlexaRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.usecase.ResetInMemoryDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetSharedPrefsDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// To make this more multiplatform compatible, we need to remove the dependency on Context
// for loading the preferences.
// TODO [#292]: Should be moved to SDK-EXT-UI module.
// TODO [#292]: https://github.com/Electric-Coin-Company/zashi-android/issues/292
class WalletViewModel(
    private val walletCoordinator: WalletCoordinator,
    private val flexaRepository: FlexaRepository,
    private val walletRepository: WalletRepository,
    private val resetInMemoryData: ResetInMemoryDataUseCase,
    private val resetSharedPrefsData: ResetSharedPrefsDataUseCase,
    private val synchronizerProvider: SynchronizerProvider,
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

    private fun clearAppStateFlow(): Flow<Boolean> =
        callbackFlow {
            viewModelScope.launch {
                val prefReset = resetSharedPrefsData()
                resetInMemoryData()
                trySend(prefReset)
            }

            awaitClose {
                // Nothing to close here
            }
        }

    fun deleteWallet(
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) = viewModelScope.launch(Dispatchers.Main) {
        Twig.info { "Delete wallet: Requested" }
        flexaRepository.disconnect()

        synchronizerProvider.getSdkSynchronizer().closeFlow().first()
        Twig.info { "Delete wallet: SDK closed" }
        val isSdkErased = walletCoordinator.deleteSdkDataFlow().first()
        Twig.info { "Delete wallet: Erase SDK result: $isSdkErased" }

        if (!isSdkErased) {
            Twig.error { "Wallet deletion failed" }
            onError()
            return@launch
        }

        val isAppErased = clearAppStateFlow().first()
        Twig.info { "Delete wallet: Erase App result: $isAppErased" }
        if (isAppErased) {
            Twig.info { "Wallet deleted successfully" }
            onSuccess()
        } else {
            Twig.error { "Wallet deletion failed" }
            onError()
        }
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
