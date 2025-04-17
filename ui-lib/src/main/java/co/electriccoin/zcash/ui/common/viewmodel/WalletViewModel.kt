package co.electriccoin.zcash.ui.common.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.usecase.IsFlexaAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetInMemoryDataUseCase
import co.electriccoin.zcash.ui.common.usecase.ResetSharedPrefsDataUseCase
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import com.flexa.core.Flexa
import com.flexa.identity.buildIdentity
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
@Suppress("LongParameterList", "TooManyFunctions")
class WalletViewModel(
    application: Application,
    private val walletCoordinator: WalletCoordinator,
    private val walletRepository: WalletRepository,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val getAvailableServers: GetDefaultServersProvider,
    private val resetInMemoryData: ResetInMemoryDataUseCase,
    private val resetSharedPrefsData: ResetSharedPrefsDataUseCase,
    private val isFlexaAvailable: IsFlexaAvailableUseCase,
    private val getSynchronizer: GetSynchronizerUseCase,
) : AndroidViewModel(application) {
    val synchronizer = walletRepository.synchronizer

    val secretState: StateFlow<SecretState> = walletRepository.secretState

    val currentWalletSnapshot: StateFlow<WalletSnapshot?> = walletRepository.currentWalletSnapshot

    fun persistNewWalletAndRestoringState(state: WalletRestoringState) {
        val application = getApplication<Application>()

        viewModelScope.launch {
            val zcashNetwork = ZcashNetwork.fromResources(application)
            val newWallet =
                PersistableWallet.new(
                    application = application,
                    zcashNetwork = zcashNetwork,
                    endpoint = getAvailableServers().first(),
                    walletInitMode = WalletInitMode.NewWallet
                )
            walletRepository.persistWallet(newWallet)

            StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(
                standardPreferenceProvider(),
                state.toNumber()
            )
        }
    }

    /**
     * Asynchronously notes that the user has completed the backup steps, which means the wallet
     * is ready to use.  Clients observe [secretState] to see the side effects.  This would be used
     * for a user creating a new wallet.
     */
    fun persistOnboardingState(onboardingState: OnboardingState) {
        walletRepository.persistOnboardingState(onboardingState)
    }

    fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    ) {
        walletRepository.persistExistingWalletWithSeedPhrase(network, seedPhrase, birthday)
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
        disconnectFlexa()

        getSynchronizer.getSdkSynchronizer()?.closeFlow()?.first()
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

    private suspend inline fun disconnectFlexa() {
        if (isFlexaAvailable()) {
            Flexa.buildIdentity().build().disconnect()
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
