package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.RestoreTimestampDataSource
import co.electriccoin.zcash.ui.common.model.FastestServersState
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.provider.WalletRestoringStateProvider
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.chooseserver.AvailableServerProvider
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
    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer: StateFlow<Synchronizer?>
    val secretState: StateFlow<SecretState>
    val fastestServers: StateFlow<FastestServersState>
    val onboardingState: Flow<OnboardingState>

    val allAccounts: Flow<List<WalletAccount>?>
    val currentAccount: Flow<WalletAccount?>

    /**
     * A flow of the wallet block synchronization state.
     */
    val walletRestoringState: StateFlow<WalletRestoringState>

    fun persistWallet(persistableWallet: PersistableWallet)

    fun persistOnboardingState(onboardingState: OnboardingState)

    fun refreshFastestServers()

    suspend fun getSelectedServer(): LightWalletEndpoint

    suspend fun getAllServers(): List<LightWalletEndpoint>

    suspend fun getSynchronizer(): Synchronizer

    fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    )
}

class WalletRepositoryImpl(
    accountDataSource: AccountDataSource,
    configurationRepository: ConfigurationRepository,
    private val persistableWalletProvider: PersistableWalletProvider,
    private val synchronizerProvider: SynchronizerProvider,
    private val getDefaultServers: GetDefaultServersProvider,
    private val standardPreferenceProvider: StandardPreferenceProvider,
    private val restoreTimestampDataSource: RestoreTimestampDataSource,
    private val walletRestoringStateProvider: WalletRestoringStateProvider,
) : WalletRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val walletMutex = Mutex()

    private val refreshFastestServersRequest = MutableSharedFlow<Unit>(replay = 1)

    /**
     * A flow of the wallet onboarding state.
     */
    override val onboardingState =
        flow {
            emitAll(
                StandardPreferenceKeys.ONBOARDING_STATE.observe(standardPreferenceProvider()).map { persistedNumber ->
                    OnboardingState.fromNumber(persistedNumber)
                }
            )
        }
    override val currentAccount: Flow<WalletAccount?> = accountDataSource.selectedAccount

    override val synchronizer: StateFlow<Synchronizer?> = synchronizerProvider.synchronizer

    override val allAccounts: StateFlow<List<WalletAccount>?> = accountDataSource.allAccounts

    override val secretState: StateFlow<SecretState> =
        combine(configurationRepository.configurationFlow, onboardingState) { config, onboardingState ->
            if (config == null) {
                SecretState.LOADING
            } else {
                when (onboardingState) {
                    OnboardingState.NEEDS_WARN,
                    OnboardingState.NEEDS_BACKUP,
                    OnboardingState.NONE -> SecretState.NONE

                    OnboardingState.READY -> SecretState.READY
                }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = SecretState.LOADING
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
                        ?.getFastestServers(getDefaultServers())
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
                }.onEach {
                    previousFastestServerState = it
                    send(it)
                }.launchIn(this)
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = FastestServersState(servers = emptyList(), isLoading = true)
        )

    /**
     * A flow of the wallet block synchronization state.
     */
    override val walletRestoringState: StateFlow<WalletRestoringState> =
        walletRestoringStateProvider
            .observe()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = WalletRestoringState.NONE
            )

    /**
     * Persists a wallet asynchronously.  Clients observe [secretState] to see the side effects.
     */
    override fun persistWallet(persistableWallet: PersistableWallet) {
        scope.launch {
            walletMutex.withLock {
                persistWalletInternal(persistableWallet)
            }
        }
    }

    private suspend fun persistWalletInternal(persistableWallet: PersistableWallet) {
        synchronizer.value?.let { (it as? SdkSynchronizer)?.close() }
        persistableWalletProvider.store(persistableWallet)
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
                persistOnboardingStateInternal(onboardingState)
            }
        }
    }

    private suspend fun WalletRepositoryImpl.persistOnboardingStateInternal(onboardingState: OnboardingState) {
        StandardPreferenceKeys.ONBOARDING_STATE.putValue(
            standardPreferenceProvider(),
            onboardingState
                .toNumber()
        )
    }

    override fun refreshFastestServers() {
        scope.launch {
            if (!fastestServers.first().isLoading) {
                refreshFastestServersRequest.emit(Unit)
            }
        }
    }

    override suspend fun getSelectedServer(): LightWalletEndpoint =
        persistableWalletProvider.persistableWallet
            .map {
                it?.endpoint
            }.filterNotNull()
            .first()

    override suspend fun getAllServers(): List<LightWalletEndpoint> {
        val defaultServers = getDefaultServers()
        val selectedServer = getSelectedServer()

        return if (defaultServers.contains(selectedServer)) {
            defaultServers
        } else {
            defaultServers + selectedServer
        }
    }

    override suspend fun getSynchronizer(): Synchronizer = synchronizerProvider.getSynchronizer()

    override fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    ) {
        scope.launch {
            walletMutex.withLock {
                val restoredWallet =
                    PersistableWallet(
                        network = network,
                        birthday = birthday,
                        endpoint = AvailableServerProvider.getDefaultServer(),
                        seedPhrase = seedPhrase,
                        walletInitMode = WalletInitMode.RestoreWallet
                    )
                persistWalletInternal(restoredWallet)
                walletRestoringStateProvider.store(WalletRestoringState.RESTORING)
                restoreTimestampDataSource.getOrCreate()
                persistOnboardingStateInternal(OnboardingState.READY)
            }
        }
    }
}
