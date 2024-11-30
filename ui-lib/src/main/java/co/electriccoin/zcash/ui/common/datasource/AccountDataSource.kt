package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.provider.SelectedAccountUUIDProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

interface AccountDataSource {

    val allAccounts: StateFlow<List<WalletAccount>?>

    val selectedAccount: StateFlow<WalletAccount?>

    suspend fun getAllAccounts(): List<WalletAccount>

    suspend fun getSelectedAccount(): WalletAccount

    suspend fun getZashiAccount(): WalletAccount.Zashi

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
            synchronizer.getAccounts().mapIndexed { index, account ->
                val balance = walletBalances.getValue(account)
                WalletAccount.Zashi(
                    sdkAccount = account,
                    unifiedAddress = WalletAddress.Unified.new(synchronizer.getUnifiedAddress(account)),
                    saplingAddress = WalletAddress.Sapling.new(synchronizer.getSaplingAddress(account)),
                    transparentAddress = WalletAddress.Transparent.new(synchronizer.getTransparentAddress(account)),
                    orchardBalance = balance.orchard,
                    transparentBalance = balance.unshielded,
                    saplingBalance = balance.sapling,
                    isSelected = index == 0 && uuid == null || account.accountUuid.contentEquals(uuid)
                )
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = null
    )

    override val selectedAccount: StateFlow<WalletAccount?> = allAccounts
        .map { account ->
            account?.firstOrNull { it.isSelected }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    override suspend fun getAllAccounts() = allAccounts.filterNotNull().first()

    override suspend fun getSelectedAccount() = selectedAccount.filterNotNull().first()

    override suspend fun getZashiAccount() = allAccounts.filterNotNull().filterIsInstance<WalletAccount.Zashi>().first()

    override suspend fun selectAccount(account: WalletAccount) = withContext(NonCancellable) {
        selectedAccountUUIDProvider.setUUID(account.sdkAccount.accountUuid)
    }
}