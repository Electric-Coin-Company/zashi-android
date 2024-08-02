package co.electriccoin.zcash.ui.common.repository

import android.app.Application
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.preference.api.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.StandardPreferenceProvider
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.usecase.AvailableServersProvider
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface WalletRepository {
    val synchronizer: StateFlow<Synchronizer?>
    val secretState: StateFlow<SecretState?>
    val fastestServers: StateFlow<FastestServersResult>

    fun closeSynchronizer()

    fun persistWallet(persistableWallet: PersistableWallet)

    fun persistOnboardingState(onboardingState: OnboardingState)

    fun refreshFastestServers()
}

class WalletRepositoryImpl(
    walletCoordinator: WalletCoordinator,
    private val application: Application,
    private val getAvailableServers: AvailableServersProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val persistableWalletPreference: PersistableWalletPreferenceDefault,
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
) : WalletRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val persistWalletMutex = Mutex()

    private val refreshFastestServersRequest = MutableSharedFlow<Unit>(replay = 1)

    /**
     * A flow of the wallet onboarding state.
     */
    private val onboardingState =
        flow {
            emitAll(
                StandardPreferenceKeys.ONBOARDING_STATE.observe(standardPreferenceProvider).map { persistedNumber ->
                    OnboardingState.fromNumber(persistedNumber)
                }
            )
        }

    override val synchronizer: StateFlow<Synchronizer?> =
        walletCoordinator.synchronizer.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    override val secretState: StateFlow<SecretState> =
        combine(
            walletCoordinator.persistableWallet,
            onboardingState
        ) { persistableWallet: PersistableWallet?, onboardingState: OnboardingState ->
            when {
                onboardingState == OnboardingState.NONE -> SecretState.None
                onboardingState == OnboardingState.NEEDS_WARN -> SecretState.NeedsWarning
                onboardingState == OnboardingState.NEEDS_BACKUP && persistableWallet != null -> {
                    SecretState.NeedsBackup(persistableWallet)
                }

                onboardingState == OnboardingState.READY && persistableWallet != null -> {
                    SecretState.Ready(persistableWallet)
                }

                else -> SecretState.None
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = SecretState.Loading
        )

    private var previousFastestServerResult: FastestServersResult? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override val fastestServers = combine(
        refreshFastestServersRequest.onStart { emit(Unit) },
        synchronizer
    ) { _, synchronizer -> synchronizer }
        .withIndex()
        .flatMapLatest { (_, synchronizer) ->
            synchronizer
                ?.getFastestServers(application, getAvailableServers())
                ?.map {
                    if (it.servers == null && it.isLoading) {
                        previousFastestServerResult?.copy(isLoading = true) ?: it
                    } else {
                        it
                    }
                }
                ?.onEach {
                    previousFastestServerResult = it
                } ?: emptyFlow()
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = FastestServersResult(servers = emptyList(), isLoading = true)
        )

    override fun closeSynchronizer() {
        scope.launch {
            synchronizer.value?.let {
                (it as SdkSynchronizer).close()
            }
        }
    }

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState] to see the side effects.
     */
    override fun persistWallet(persistableWallet: PersistableWallet) {
        scope.launch {
            persistWalletMutex.withLock {
                persistableWalletPreference.putValue(encryptedPreferenceProvider, persistableWallet)
            }
        }
    }

    /**
     * Asynchronously notes that the user has completed the backup steps, which means the wallet
     * is ready to use.  Clients observe [secretState] to see the side effects.  This would be used
     * for a user creating a new wallet.
     */
    override fun persistOnboardingState(onboardingState: OnboardingState) {
        scope.launch {
            // Use the Mutex here to avoid timing issues.  During wallet restore, persistOnboardingState()
            // is called prior to persistExistingWallet().  Although persistOnboardingState() should
            // complete quickly, it isn't guaranteed to complete before persistExistingWallet()
            // unless a mutex is used here.
            persistWalletMutex.withLock {
                StandardPreferenceKeys.ONBOARDING_STATE.putValue(standardPreferenceProvider, onboardingState.toNumber())
            }
        }
    }

    override fun refreshFastestServers() {
        scope.launch {
            if (!fastestServers.first().isLoading) {
                refreshFastestServersRequest.emit(Unit)
            }
        }
    }
}
