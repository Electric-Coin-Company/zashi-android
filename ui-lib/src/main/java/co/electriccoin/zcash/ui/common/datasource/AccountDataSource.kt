package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.WalletBalance
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.time.Duration

interface AccountDataSource {

    val allAccounts: StateFlow<List<WalletAccount>?>

    val selectedAccount: Flow<WalletAccount?>

    val zashiAccount: Flow<ZashiAccount?>

    val keystoneAccount: Flow<KeystoneAccount?>

    suspend fun getAllAccounts(): List<WalletAccount>

    suspend fun getSelectedAccount(): WalletAccount

    suspend fun getZashiAccount(): ZashiAccount

    suspend fun getKeystoneAccount(): KeystoneAccount

    suspend fun selectAccount(account: Account)

    suspend fun selectAccount(account: WalletAccount)
}

class AccountDataSourceImpl(
    synchronizerProvider: SynchronizerProvider,
    private val selectedAccountUUIDProvider: SelectedAccountUUIDProvider,
) : AccountDataSource {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val internalAccounts: Flow<List<InternalAccountWithBalances>?> =
        synchronizerProvider
            .synchronizer
            .flatMapLatest { synchronizer ->
                synchronizer
                    ?.accountsFlow
                    ?.map { accounts ->
                        accounts?.map { account ->
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
                    ?.flatMapLatest { accountsWithAddresses ->
                        if (accountsWithAddresses == null) {
                            flowOf(null)
                        } else {
                            synchronizer.walletBalances.map { walletBalances ->
                                if (walletBalances == null) {
                                    null
                                } else {
                                    accountsWithAddresses.map { accountWithAddresses ->
                                        val balance = walletBalances.getValue(accountWithAddresses.sdkAccount)

                                        InternalAccountWithBalances(
                                            sdkAccount = accountWithAddresses.sdkAccount,
                                            unifiedAddress = accountWithAddresses.unifiedAddress,
                                            saplingAddress = accountWithAddresses.saplingAddress,
                                            transparentAddress = accountWithAddresses.transparentAddress,
                                            orchardBalance = balance.orchard,
                                            transparentBalance = balance.unshielded,
                                            saplingBalance = balance.sapling,
                                        )
                                    }
                                }
                            }
                        }
                    } ?: flowOf(null)
            }

    override val allAccounts: StateFlow<List<WalletAccount>?> = combine(
        internalAccounts,
        selectedAccountUUIDProvider.uuid,
    ) { accounts, uuid ->
        accounts?.mapIndexedNotNull { index, account ->
            when (val keysource = account.sdkAccount.keySource?.lowercase()) {
                "keystone" ->
                    KeystoneAccount(
                        sdkAccount = account.sdkAccount,
                        unifiedAddress = account.unifiedAddress,
                        saplingAddress = account.saplingAddress,
                        transparentAddress = account.transparentAddress,
                        orchardBalance = account.orchardBalance,
                        transparentBalance = account.transparentBalance,
                        saplingBalance = account.saplingBalance,
                        isSelected = index == 0 && uuid == null || account.sdkAccount.accountUuid.contentEquals(uuid),
                        name = account.sdkAccount.accountName?.let { stringRes(it) } ?: stringRes("Keystone"),
                    )

                null, "zashi" -> ZashiAccount(
                    sdkAccount = account.sdkAccount,
                    unifiedAddress = account.unifiedAddress,
                    saplingAddress = account.saplingAddress,
                    transparentAddress = account.transparentAddress,
                    orchardBalance = account.orchardBalance,
                    transparentBalance = account.transparentBalance,
                    saplingBalance = account.saplingBalance,
                    isSelected = index == 0 && uuid == null || account.sdkAccount.accountUuid.contentEquals(uuid),
                    name = account.sdkAccount.accountName?.let { stringRes(it) } ?: stringRes("Zashi"),
                )

                else -> {
                    Twig.error { "account with keysource $keysource not supported" }
                    null
                }
            }
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT, Duration.ZERO),
            initialValue = null
        )

    override val selectedAccount: Flow<WalletAccount?> = allAccounts
        .map { account ->
            account?.firstOrNull { it.isSelected }
        }

    override val zashiAccount: Flow<ZashiAccount?> = allAccounts
        .map { account ->
            account?.filterIsInstance<ZashiAccount>()?.firstOrNull()
        }

    override val keystoneAccount: Flow<KeystoneAccount?> = allAccounts
        .map { account ->
            account?.filterIsInstance<KeystoneAccount>()?.firstOrNull()
        }

    override suspend fun getAllAccounts() = allAccounts.filterNotNull().first()

    override suspend fun getSelectedAccount() = selectedAccount.filterNotNull().first()

    override suspend fun getZashiAccount() = zashiAccount.filterNotNull().first()

    override suspend fun getKeystoneAccount() = keystoneAccount.filterNotNull().first()

    override suspend fun selectAccount(account: Account) = withContext(NonCancellable) {
        selectedAccountUUIDProvider.setUUID(account.accountUuid)
    }

    override suspend fun selectAccount(account: WalletAccount) {
        selectedAccountUUIDProvider.setUUID(account.sdkAccount.accountUuid)
    }
}

private data class InternalAccountWithAddresses(
    val sdkAccount: Account,
    val unifiedAddress: WalletAddress.Unified,
    val saplingAddress: WalletAddress.Sapling,
    val transparentAddress: WalletAddress.Transparent,
)

private data class InternalAccountWithBalances(
    val sdkAccount: Account,
    val unifiedAddress: WalletAddress.Unified,
    val saplingAddress: WalletAddress.Sapling,
    val transparentAddress: WalletAddress.Transparent,
    val saplingBalance: WalletBalance,
    val orchardBalance: WalletBalance,
    val transparentBalance: Zatoshi,
)