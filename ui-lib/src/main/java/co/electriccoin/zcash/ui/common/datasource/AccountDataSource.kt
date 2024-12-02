package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface AccountDataSource {

    val allAccounts: StateFlow<List<WalletAccount>?>

    val selectedAccount: Flow<WalletAccount?>

    val zashiAccount: Flow<ZashiAccount?>

    val keystoneAccount: Flow<KeystoneAccount?>

    suspend fun getAllAccounts(): List<WalletAccount>

    suspend fun getSelectedAccount(): WalletAccount

    suspend fun getZashiAccount(): ZashiAccount

    suspend fun selectAccount(account: WalletAccount)
}

class AccountDataSourceImpl(
    walletCoordinator: WalletCoordinator,
    private val selectedAccountUUIDProvider: SelectedAccountUUIDProvider,
) : AccountDataSource {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val walletBalances = walletCoordinator.synchronizer.flatMapLatest {
        it?.walletBalances ?: emptyFlow()
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = null
    )

    override val allAccounts: StateFlow<List<WalletAccount>?> = combine(
        walletCoordinator.synchronizer,
        selectedAccountUUIDProvider.uuid,
        walletBalances,
    ) { synchronizer, uuid, walletBalances ->

        if (synchronizer == null || walletBalances == null) {
            null
        } else {
            synchronizer.getAccountsSafe().mapIndexed { index, account ->
                val balance = walletBalances.getValue(account)

                ZashiAccount(
                    sdkAccount = account,
                    unifiedAddress = WalletAddress.Unified.new(synchronizer.getUnifiedAddress(account)),
                    saplingAddress = WalletAddress.Sapling.new(synchronizer.getSaplingAddress(account)),
                    transparentAddress = WalletAddress.Transparent.new(synchronizer.getTransparentAddress(account)),
                    orchardBalance = balance.orchard,
                    transparentBalance = balance.unshielded,
                    saplingBalance = balance.sapling,
                    isSelected = index == 0 && uuid == null || account.accountUuid.contentEquals(uuid),
                )
            }
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT, Duration.ZERO),
            initialValue = null
        )

    private suspend fun Synchronizer.getAccountsSafe(): List<Account> {
        var accounts: List<Account>? = null

        while (accounts == null) {
            try {
                accounts = getAccounts()
            } catch (_: Throwable) {
                delay(1.seconds)
            }
        }

        return accounts
    }

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

    override suspend fun selectAccount(account: WalletAccount) = withContext(NonCancellable) {
        selectedAccountUUIDProvider.setUUID(account.sdkAccount.accountUuid)
    }
}