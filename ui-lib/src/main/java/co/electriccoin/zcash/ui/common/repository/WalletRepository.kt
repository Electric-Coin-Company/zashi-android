package co.electriccoin.zcash.ui.common.repository

import android.app.Application
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.WalletAddresses
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.FastestServersState
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
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
    val fastestServers: StateFlow<FastestServersState>
    val persistableWallet: Flow<PersistableWallet?>
    val addresses: StateFlow<WalletAddresses?>

    fun persistWallet(persistableWallet: PersistableWallet)

    fun persistOnboardingState(onboardingState: OnboardingState)

    fun refreshFastestServers()

    suspend fun getSelectedServer(): LightWalletEndpoint

    suspend fun getAllServers(): List<LightWalletEndpoint>

    suspend fun getSynchronizer(): Synchronizer
}

class WalletRepositoryImpl(
    walletCoordinator: WalletCoordinator,
    private val application: Application,
    private val getDefaultServers: GetDefaultServersProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val persistableWalletPreference: PersistableWalletPreferenceDefault,
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider,
) : WalletRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val walletMutex = Mutex()

    private val refreshFastestServersRequest = MutableSharedFlow<Unit>(replay = 1)

    /**
     * A flow of the wallet onboarding state.
     */
    private val onboardingState =
        flow {
            emitAll(
                StandardPreferenceKeys.ONBOARDING_STATE.observe(standardPreferenceProvider()).map { persistedNumber ->
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override val fastestServers =
        channelFlow {
            var previousFastestServerState: FastestServersState? = null

            combine(
                refreshFastestServersRequest.onStart { emit(Unit) },
                synchronizer
            ) { _, synchronizer -> synchronizer }
                .withIndex()
                .flatMapLatest { (_, synchronizer) ->
                    synchronizer
                        ?.getFastestServers(application, getDefaultServers())
                        ?.map {
                            when (it) {
                                FastestServersResult.Measuring ->
                                    previousFastestServerState?.copy(isLoading = true)
                                        ?: FastestServersState(servers = null, isLoading = true)

                                is FastestServersResult.Validating ->
                                    FastestServersState(servers = it.servers, isLoading = true)

                                is FastestServersResult.Done ->
                                    FastestServersState(servers = it.servers, isLoading = false)
                            }
                        } ?: emptyFlow()
                }
                .onEach {
                    previousFastestServerState = it
                    send(it)
                }
                .launchIn(this)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = FastestServersState(servers = emptyList(), isLoading = true)
        )

    override val persistableWallet: Flow<PersistableWallet?> =
        secretState.map {
            (it as? SecretState.Ready?)?.persistableWallet
        }

    override val addresses: StateFlow<WalletAddresses?> =
        synchronizer
            .filterNotNull()
            .map {
                runCatching {
                    WalletAddresses.new(it)
                }.onFailure {
                    Twig.warn { "Wait until the SDK starts providing the addresses" }
                }.getOrNull()
            }.stateIn(
                scope,
                SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                null
            )

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState] to see the side effects.
     */
    override fun persistWallet(persistableWallet: PersistableWallet) {
        scope.launch {
            walletMutex.withLock {
                synchronizer.value?.let { (it as? SdkSynchronizer)?.close() }
                persistableWalletPreference.putValue(encryptedPreferenceProvider(), persistableWallet)
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
            walletMutex.withLock {
                StandardPreferenceKeys.ONBOARDING_STATE.putValue(
                    standardPreferenceProvider(),
                    onboardingState
                        .toNumber()
                )
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

    override suspend fun getSelectedServer(): LightWalletEndpoint {
        return persistableWallet
            .map {
                it?.endpoint
            }
            .filterNotNull()
            .first()
    }

    override suspend fun getAllServers(): List<LightWalletEndpoint> {
        val defaultServers = getDefaultServers()
        val selectedServer = getSelectedServer()

        return if (defaultServers.contains(selectedServer)) {
            defaultServers
        } else {
            defaultServers + selectedServer
        }
    }

    override suspend fun getSynchronizer(): Synchronizer = synchronizer.filterNotNull().first()
}
