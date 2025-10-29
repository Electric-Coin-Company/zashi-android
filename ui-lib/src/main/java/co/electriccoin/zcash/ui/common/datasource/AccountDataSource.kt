package co.electriccoin.zcash.ui.common.datasource

import android.content.Context
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.AccountImportSetup
import cash.z.ecc.android.sdk.model.AccountPurpose
import cash.z.ecc.android.sdk.model.AccountUuid
import cash.z.ecc.android.sdk.model.UnifiedAddressRequest
import cash.z.ecc.android.sdk.model.UnifiedFullViewingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.Zip32AccountIndex
import cash.z.ecc.sdk.extension.ZERO
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SaplingInfo
import co.electriccoin.zcash.ui.common.model.TransparentInfo
import co.electriccoin.zcash.ui.common.model.UnifiedInfo
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.design.util.combineToFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

interface AccountDataSource {
    val allAccounts: StateFlow<List<WalletAccount>?>

    val selectedAccount: Flow<WalletAccount?>

    val zashiAccount: Flow<ZashiAccount?>

    suspend fun getAllAccounts(): List<WalletAccount>

    suspend fun getSelectedAccount(): WalletAccount

    suspend fun getZashiAccount(): ZashiAccount

    suspend fun selectAccount(account: Account)

    suspend fun selectAccount(account: WalletAccount)

    suspend fun importKeystoneAccount(ufvk: String, seedFingerprint: String, index: Long): Account

    suspend fun requestNextShieldedAddress()
}

@Suppress("TooManyFunctions")
class AccountDataSourceImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val selectedAccountUUIDProvider: SelectedAccountUUIDProvider,
    private val context: Context,
) : AccountDataSource {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val requestNextShieldedAddressPipeline = MutableSharedFlow<AccountUuid>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val allAccounts: StateFlow<List<WalletAccount>?> =
        synchronizerProvider
            .synchronizer
            .flatMapLatest { synchronizer ->
                synchronizer
                    ?.accountsFlow
                    ?.filterNotNull()
                    ?.flatMapLatest { allSdkAccounts ->
                        allSdkAccounts
                            .map { sdkAccount ->
                                combine(
                                    observeUnified(synchronizer, sdkAccount),
                                    observeTransparent(synchronizer, sdkAccount),
                                    observeSapling(synchronizer, sdkAccount),
                                    observeIsSelected(sdkAccount, allSdkAccounts),
                                ) { unified, transparent, sapling, isSelected ->
                                    when (sdkAccount.keySource?.lowercase()) {
                                        KEYSTONE_KEYSOURCE ->
                                            KeystoneAccount(
                                                sdkAccount = sdkAccount,
                                                unified = unified,
                                                transparent = transparent,
                                                isSelected = isSelected,
                                            )

                                        else ->
                                            ZashiAccount(
                                                sdkAccount = sdkAccount,
                                                unified = unified,
                                                transparent = transparent,
                                                sapling = sapling!!,
                                                isSelected = isSelected,
                                            )
                                    }
                                }
                            }.combineToFlow()
                    }
                    ?: flowOf(null)
            }.map { it?.sortedDescending() }
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    override val selectedAccount: Flow<WalletAccount?> =
        allAccounts
            .map { account ->
                account?.firstOrNull { it.isSelected }
            }.distinctUntilChanged()

    override val zashiAccount: Flow<ZashiAccount?> =
        allAccounts
            .map { account ->
                account?.filterIsInstance<ZashiAccount>()?.firstOrNull()
            }.distinctUntilChanged()

    override suspend fun getAllAccounts() = withContext(Dispatchers.IO) { allAccounts.filterNotNull().first() }

    override suspend fun getSelectedAccount() = withContext(Dispatchers.IO) { selectedAccount.filterNotNull().first() }

    override suspend fun getZashiAccount() = withContext(Dispatchers.IO) { zashiAccount.filterNotNull().first() }

    override suspend fun selectAccount(account: Account) =
        withContext(Dispatchers.IO) {
            selectedAccountUUIDProvider.setUUID(account.accountUuid)
        }

    override suspend fun selectAccount(account: WalletAccount) = selectAccount(account.sdkAccount)

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun importKeystoneAccount(
        ufvk: String,
        seedFingerprint: String,
        index: Long
    ): Account =
        withContext(Dispatchers.IO) {
            synchronizerProvider
                .getSynchronizer()
                .importAccountByUfvk(
                    AccountImportSetup(
                        accountName = context.getString(R.string.keystone_wallet_name),
                        keySource = KEYSTONE_KEYSOURCE,
                        ufvk = UnifiedFullViewingKey(ufvk),
                        purpose =
                            AccountPurpose.Spending(
                                seedFingerprint = seedFingerprint.hexToByteArray(),
                                zip32AccountIndex = Zip32AccountIndex.new(index)
                            )
                    ),
                )
        }

    override suspend fun requestNextShieldedAddress() {
        scope
            .launch {
                requestNextShieldedAddressPipeline.emit(getSelectedAccount().sdkAccount.accountUuid)
            }.join()
    }

    private fun observeIsSelected(sdkAccount: Account, allAccounts: List<Account>) =
        selectedAccountUUIDProvider
            .uuid
            .map { uuid ->
                when (sdkAccount.keySource?.lowercase()) {
                    KEYSTONE_KEYSOURCE -> sdkAccount.accountUuid == uuid || allAccounts.size == 1
                    else -> uuid == null || sdkAccount.accountUuid == uuid || allAccounts.size == 1
                }
            }

    private fun observeUnified(synchronizer: Synchronizer, sdkAccount: Account): Flow<UnifiedInfo> {
        val addressFlow =
            requestNextShieldedAddressPipeline
                .onStart { emit(sdkAccount.accountUuid) }
                .map {
                    val request =
                        if (sdkAccount.keySource?.lowercase() == KEYSTONE_KEYSOURCE) {
                            UnifiedAddressRequest.Orchard
                        } else {
                            UnifiedAddressRequest.shielded
                        }

                    WalletAddress.Unified.new(synchronizer.getCustomUnifiedAddress(sdkAccount, request))
                }.retryWhen { _, attempt ->
                    delay(attempt.coerceAtMost(RETRY_DELAY).seconds)
                    true
                }

        return combine(addressFlow, synchronizer.walletBalances) { address, balances ->
            val balance = balances?.get(sdkAccount.accountUuid)
            UnifiedInfo(address = address, balance = balance?.orchard ?: createEmptyWalletBalance())
        }
    }

    private fun observeTransparent(synchronizer: Synchronizer, sdkAccount: Account): Flow<TransparentInfo> {
        val transparentAddress =
            flow {
                emit(WalletAddress.Transparent.new(synchronizer.getTransparentAddress(sdkAccount)))
            }.retryWhen { _, attempt ->
                delay(attempt.coerceAtMost(RETRY_DELAY).seconds)
                true
            }
        return combine(transparentAddress, synchronizer.walletBalances) { address, balances ->
            val balance = balances?.get(sdkAccount.accountUuid)
            TransparentInfo(address = address, balance = balance?.unshielded ?: Zatoshi.ZERO)
        }
    }

    private fun observeSapling(synchronizer: Synchronizer, sdkAccount: Account): Flow<SaplingInfo?> =
        if (sdkAccount.keySource == KEYSTONE_KEYSOURCE) {
            flowOf(null)
        } else {
            val saplingAddress =
                flow {
                    emit(WalletAddress.Sapling.new(synchronizer.getSaplingAddress(sdkAccount)))
                }.retryWhen { _, attempt ->
                    delay(attempt.coerceAtMost(RETRY_DELAY).seconds)
                    true
                }
            combine(saplingAddress, synchronizer.walletBalances) { address, balances ->
                val balance = balances?.get(sdkAccount.accountUuid)
                SaplingInfo(address = address, balance = balance?.sapling ?: createEmptyWalletBalance())
            }
        }

    private fun createEmptyWalletBalance() = WalletBalance(Zatoshi.ZERO, Zatoshi.ZERO, Zatoshi.ZERO)
}

private const val RETRY_DELAY = 3L
private const val KEYSTONE_KEYSOURCE = "keystone"
