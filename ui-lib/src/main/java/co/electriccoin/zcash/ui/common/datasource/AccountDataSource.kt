package co.electriccoin.zcash.ui.common.datasource

import android.content.Context
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.AccountImportSetup
import cash.z.ecc.android.sdk.model.AccountPurpose
import cash.z.ecc.android.sdk.model.UnifiedFullViewingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.Zip32AccountIndex
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SaplingInfo
import co.electriccoin.zcash.ui.common.model.TransparentInfo
import co.electriccoin.zcash.ui.common.model.UnifiedInfo
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

interface AccountDataSource {
    val onAccountChanged: Flow<Unit>

    val allAccounts: StateFlow<List<WalletAccount>?>

    val selectedAccount: Flow<WalletAccount?>

    val zashiAccount: Flow<ZashiAccount?>

    suspend fun getAllAccounts(): List<WalletAccount>

    suspend fun getSelectedAccount(): WalletAccount

    suspend fun getZashiAccount(): ZashiAccount

    suspend fun selectAccount(account: Account)

    suspend fun selectAccount(account: WalletAccount)

    suspend fun importKeystoneAccount(
        ufvk: String,
        seedFingerprint: String,
        index: Long
    ): Account
}

class AccountDataSourceImpl(
    private val synchronizerProvider: SynchronizerProvider,
    private val selectedAccountUUIDProvider: SelectedAccountUUIDProvider,
    private val context: Context,
) : AccountDataSource {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val onAccountChanged = MutableSharedFlow<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val internalAccounts: Flow<List<InternalAccountWithBalances>?> =
        synchronizerProvider
            .synchronizer
            .flatMapLatest { synchronizer ->
                synchronizer
                    ?.accountsFlow
                    ?.map { accounts ->
                        accounts?.map { account ->
                            if (account.keySource == KEYSTONE_KEYSOURCE) {
                                InternalAccountWithAddresses(
                                    sdkAccount = account,
                                    unifiedAddress =
                                        WalletAddress.Unified.new(synchronizer.getUnifiedAddress(account)),
                                    saplingAddress = null,
                                    transparentAddress =
                                        WalletAddress.Transparent.new(synchronizer.getTransparentAddress(account)),
                                )
                            } else {
                                InternalAccountWithAddresses(
                                    sdkAccount = account,
                                    unifiedAddress =
                                        WalletAddress.Unified.new(synchronizer.getUnifiedAddress(account)),
                                    saplingAddress =
                                        WalletAddress.Sapling.new(synchronizer.getSaplingAddress(account)),
                                    transparentAddress =
                                        WalletAddress.Transparent.new(synchronizer.getTransparentAddress(account)),
                                )
                            }
                        }
                    }?.flatMapLatest { accountsWithAddresses ->
                        if (accountsWithAddresses == null) {
                            flowOf(null)
                        } else {
                            synchronizer.walletBalances.map { walletBalances ->
                                if (walletBalances == null) {
                                    null
                                } else {
                                    accountsWithAddresses.map { accountWithAddresses ->
                                        val balance =
                                            walletBalances[accountWithAddresses.sdkAccount.accountUuid]

                                        InternalAccountWithBalances(
                                            sdkAccount = accountWithAddresses.sdkAccount,
                                            unifiedAddress = accountWithAddresses.unifiedAddress,
                                            saplingAddress = accountWithAddresses.saplingAddress,
                                            transparentAddress = accountWithAddresses.transparentAddress,
                                            orchardBalance = balance?.orchard ?: createEmptyWalletBalance(),
                                            transparentBalance = balance?.unshielded ?: Zatoshi.ZERO,
                                            saplingBalance = balance?.sapling,
                                        )
                                    }
                                }
                            }
                        }
                    }?.retryWhen { _, attempt ->
                        emit(null)
                        delay(attempt.coerceAtMost(RETRY_DELAY).seconds)
                        true
                    }
                    ?: flowOf(null)
            }.flowOn(Dispatchers.IO)

    override val allAccounts: StateFlow<List<WalletAccount>?> =
        combine(
            internalAccounts,
            selectedAccountUUIDProvider.uuid,
        ) { accounts, uuid ->
            accounts
                ?.map { account ->
                    when (account.sdkAccount.keySource?.lowercase()) {
                        KEYSTONE_KEYSOURCE ->
                            KeystoneAccount(
                                sdkAccount = account.sdkAccount,
                                unified =
                                    UnifiedInfo(
                                        address = account.unifiedAddress,
                                        balance = account.orchardBalance
                                    ),
                                transparent =
                                    TransparentInfo(
                                        address = account.transparentAddress,
                                        balance = account.transparentBalance
                                    ),
                                isSelected = account.sdkAccount.accountUuid == uuid || accounts.size == 1,
                            )

                        else ->
                            ZashiAccount(
                                sdkAccount = account.sdkAccount,
                                unified =
                                    UnifiedInfo(
                                        address = account.unifiedAddress,
                                        balance = account.orchardBalance
                                    ),
                                transparent =
                                    TransparentInfo(
                                        address = account.transparentAddress,
                                        balance = account.transparentBalance
                                    ),
                                sapling =
                                    SaplingInfo(
                                        address = account.saplingAddress!!,
                                        balance = account.saplingBalance!!
                                    ),
                                isSelected =
                                    uuid == null ||
                                        account.sdkAccount.accountUuid == uuid ||
                                        accounts.size == 1,
                            )
                    }
                }?.sortedDescending()
        }.flowOn(Dispatchers.IO)
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

    override suspend fun getAllAccounts() =
        withContext(Dispatchers.IO) {
            allAccounts.filterNotNull().first()
        }

    override suspend fun getSelectedAccount() =
        withContext(Dispatchers.IO) {
            selectedAccount.filterNotNull().first()
        }

    override suspend fun getZashiAccount() =
        withContext(Dispatchers.IO) {
            zashiAccount.filterNotNull().first()
        }

    override suspend fun selectAccount(account: Account) {
        withContext(Dispatchers.IO) {
            val current = selectedAccountUUIDProvider.getUUID()

            selectedAccountUUIDProvider.setUUID(account.accountUuid)

            scope.launch {
                if (current != account.accountUuid) {
                    onAccountChanged.emit(Unit)
                }
            }
        }
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
}

private fun createEmptyWalletBalance() =
    WalletBalance(
        available = Zatoshi.ZERO,
        changePending = Zatoshi.ZERO,
        valuePending = Zatoshi.ZERO,
    )

private val Zatoshi.Companion.ZERO: Zatoshi
    get() = Zatoshi(0)

private data class InternalAccountWithAddresses(
    val sdkAccount: Account,
    val unifiedAddress: WalletAddress.Unified,
    val saplingAddress: WalletAddress.Sapling?,
    val transparentAddress: WalletAddress.Transparent,
)

private data class InternalAccountWithBalances(
    val sdkAccount: Account,
    val unifiedAddress: WalletAddress.Unified,
    val saplingAddress: WalletAddress.Sapling?,
    val transparentAddress: WalletAddress.Transparent,
    val saplingBalance: WalletBalance?,
    val orchardBalance: WalletBalance,
    val transparentBalance: Zatoshi,
)

private const val RETRY_DELAY = 3L
private const val KEYSTONE_KEYSOURCE = "keystone"
