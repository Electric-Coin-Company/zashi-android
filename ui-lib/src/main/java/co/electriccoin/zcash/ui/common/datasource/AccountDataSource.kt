package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
        walletCoordinator.persistableWallet,
        selectedAccountUUIDProvider.uuid,
        walletBalances,
    ) { synchronizer, persistableWallet, uuid, walletBalances ->

        if (synchronizer == null || walletBalances == null || persistableWallet == null) {
            null
        } else {
            synchronizer.getAccounts().mapIndexed { index, account ->
                val balance = walletBalances.getValue(account)
                val spendingKey = deriveSpendingKey(persistableWallet)

                ZashiAccount(
                    sdkAccount = account,
                    unifiedAddress = WalletAddress.Unified.new(synchronizer.getUnifiedAddress(account)),
                    saplingAddress = WalletAddress.Sapling.new(synchronizer.getSaplingAddress(account)),
                    transparentAddress = WalletAddress.Transparent.new(synchronizer.getTransparentAddress(account)),
                    orchardBalance = balance.orchard,
                    transparentBalance = balance.unshielded,
                    saplingBalance = balance.sapling,
                    isSelected = index == 0 && uuid == null || account.accountUuid.contentEquals(uuid),
                    persistableWallet = persistableWallet,
                    spendingKey = spendingKey
                )
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT, Duration.ZERO),
        initialValue = null
    )

    private suspend fun deriveSpendingKey(persistableWallet: PersistableWallet): UnifiedSpendingKey? {
        // crashes currently
        // val bip39Seed =
        //     withContext(Dispatchers.IO) {
        //         Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
        //     }
        // val spendingKey = DerivationTool.getInstance().deriveUnifiedSpendingKey(
        //     seed = bip39Seed,
        //     network = persistableWallet.network,
        //     accountIndex = 0,
        // )
        // return spendingKey
        return null
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