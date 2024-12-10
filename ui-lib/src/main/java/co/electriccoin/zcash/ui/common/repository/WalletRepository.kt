package co.electriccoin.zcash.ui.common.repository

import android.app.Application
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletInitMode
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.FastestServersResult
import cash.z.ecc.android.sdk.model.PercentDecimal
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.extension.throttle
import co.electriccoin.zcash.ui.common.model.FastestServersState
import co.electriccoin.zcash.ui.common.model.OnboardingState
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.viewmodel.SecretState
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.screen.chooseserver.AvailableServerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds

interface WalletRepository {
    /**
     * Synchronizer that is retained long enough to survive configuration changes.
     */
    val synchronizer: StateFlow<Synchronizer?>
    val secretState: StateFlow<SecretState>
    val fastestServers: StateFlow<FastestServersState>
    val persistableWallet: Flow<PersistableWallet?>
    val onboardingState: Flow<OnboardingState>

    val allAccounts: Flow<List<WalletAccount>?>
    val currentAccount: Flow<WalletAccount?>
    val currentWalletSnapshot: StateFlow<WalletSnapshot?>

    /**
     * A flow of the wallet block synchronization state.
     */
    val walletRestoringState: StateFlow<WalletRestoringState>

    /**
     * A flow of the wallet current state information that should be displayed in screens top app bar.
     */
    val walletStateInformation: StateFlow<TopAppBarSubTitleState>

    fun persistWallet(persistableWallet: PersistableWallet)

    fun persistOnboardingState(onboardingState: OnboardingState)

    fun refreshFastestServers()

    suspend fun getSelectedServer(): LightWalletEndpoint

    suspend fun getAllServers(): List<LightWalletEndpoint>

    suspend fun getSynchronizer(): Synchronizer

    suspend fun getPersistableWallet(): PersistableWallet

    fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    )
}

class WalletRepositoryImpl(
    accountDataSource: AccountDataSource,
    persistableWalletProvider: PersistableWalletProvider,
    private val synchronizerProvider: SynchronizerProvider,
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
        combine(
            persistableWalletProvider.persistableWallet,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentWalletSnapshot: StateFlow<WalletSnapshot?> =
        combine(synchronizer, currentAccount) { synchronizer, currentAccount ->
            synchronizer to currentAccount
        }
            .flatMapLatest { (synchronizer, currentAccount) ->
                if (synchronizer == null || currentAccount == null) {
                    flowOf(null)
                } else {
                    toWalletSnapshot(synchronizer, currentAccount)
                }
            }
            .throttle(1.seconds)
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    /**
     * A flow of the wallet block synchronization state.
     */
    override val walletRestoringState: StateFlow<WalletRestoringState> =
        flow {
            emitAll(
                StandardPreferenceKeys.WALLET_RESTORING_STATE
                    .observe(standardPreferenceProvider()).map { persistedNumber ->
                        WalletRestoringState.fromNumber(persistedNumber)
                    }
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = WalletRestoringState.NONE
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val walletStateInformation: StateFlow<TopAppBarSubTitleState> =
        synchronizer
            .filterNotNull()
            .flatMapLatest { synchronizer ->
                combine(
                    synchronizer.status,
                    walletRestoringState
                ) { status: Synchronizer.Status?, walletRestoringState: WalletRestoringState ->
                    if (Synchronizer.Status.DISCONNECTED == status) {
                        TopAppBarSubTitleState.Disconnected
                    } else if (WalletRestoringState.RESTORING == walletRestoringState) {
                        TopAppBarSubTitleState.Restoring
                    } else {
                        TopAppBarSubTitleState.None
                    }
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = TopAppBarSubTitleState.None
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
        persistableWalletPreference.putValue(encryptedPreferenceProvider(), persistableWallet)
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

    override suspend fun getSynchronizer(): Synchronizer = synchronizerProvider.getSynchronizer()

    override suspend fun getPersistableWallet(): PersistableWallet = persistableWallet.filterNotNull().first()

    override fun persistExistingWalletWithSeedPhrase(
        network: ZcashNetwork,
        seedPhrase: SeedPhrase,
        birthday: BlockHeight?
    ) {
        scope.launch {
            walletMutex.withLock {
                persistOnboardingStateInternal(OnboardingState.READY)

                val restoredWallet =
                    PersistableWallet(
                        network = network,
                        birthday = birthday,
                        endpoint = AvailableServerProvider.getDefaultServer(),
                        seedPhrase = seedPhrase,
                        walletInitMode = WalletInitMode.RestoreWallet
                    )
                persistWalletInternal(restoredWallet)
                StandardPreferenceKeys.WALLET_RESTORING_STATE.putValue(
                    standardPreferenceProvider(),
                    WalletRestoringState.RESTORING.toNumber()
                )
            }
        }
    }
}

private fun Synchronizer.toCommonError(): Flow<SynchronizerError?> =
    callbackFlow {
        // just for initial default value emit
        trySend(null)

        onCriticalErrorHandler = {
            Twig.error { "WALLET - Error Critical: $it" }
            trySend(SynchronizerError.Critical(it))
            false
        }
        onProcessorErrorHandler = {
            Twig.error { "WALLET - Error Processor: $it" }
            trySend(SynchronizerError.Processor(it))
            false
        }
        onSubmissionErrorHandler = {
            Twig.error { "WALLET - Error Submission: $it" }
            trySend(SynchronizerError.Submission(it))
            false
        }
        onSetupErrorHandler = {
            Twig.error { "WALLET - Error Setup: $it" }
            trySend(SynchronizerError.Setup(it))
            false
        }
        onChainErrorHandler = { x, y ->
            Twig.error { "WALLET - Error Chain: $x, $y" }
            trySend(SynchronizerError.Chain(x, y))
        }

        awaitClose {
            // nothing to close here
        }
    }

// No good way around needing magic numbers for the indices
@Suppress("MagicNumber")
private fun toWalletSnapshot(
    synchronizer: Synchronizer,
    account: WalletAccount
) = combine(
    // 0
    synchronizer.status,
    // 1
    synchronizer.processorInfo,
    // 2
    synchronizer.progress,
    // 3
    synchronizer.toCommonError()
) { flows ->
    val progressPercentDecimal = (flows[2] as PercentDecimal)

    WalletSnapshot(
        status = flows[0] as Synchronizer.Status,
        processorInfo = flows[1] as CompactBlockProcessor.ProcessorInfo,
        orchardBalance = account.unified.balance,
        saplingBalance = account.sapling?.balance,
        transparentBalance = account.transparent.balance,
        progress = progressPercentDecimal,
        synchronizerError = flows[3] as SynchronizerError?
    )
}
