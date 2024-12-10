package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.TransactionOverview
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

interface TransactionRepository {
    val currentTransactions: Flow<List<TransactionOverview>?>
}

class TransactionRepositoryImpl(
    synchronizerProvider: SynchronizerProvider,
    accountDataSource: AccountDataSource
) : TransactionRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentTransactions: Flow<List<TransactionOverview>?> =
        combine(synchronizerProvider.synchronizer, accountDataSource.selectedAccount) { synchronizer, account ->
            synchronizer to account
        }.flatMapLatest { (synchronizer, account) ->
            if (synchronizer == null || account == null) {
                flowOf(null)
            } else {
                synchronizer.getTransactions(account.sdkAccount.accountUuid)
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(Duration.ZERO, Duration.ZERO),
            initialValue = null
        )
}
