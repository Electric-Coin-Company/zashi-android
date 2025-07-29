package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface ShieldFundsRepository {
    val availability: Flow<ShieldFundsData>
}

class ShieldFundsRepositoryImpl(
    accountDataSource: AccountDataSource,
    synchronizerProvider: SynchronizerProvider,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
) : ShieldFundsRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val availability: Flow<ShieldFundsData> =
        combine(
            synchronizerProvider.synchronizer.flatMapLatest { it?.status ?: flowOf(null) },
            accountDataSource.selectedAccount
        ) { status, account ->
            status to account
        }.flatMapLatest { (status, account) ->
            when {
                status in listOf(null, Synchronizer.Status.INITIALIZING) -> flowOf(ShieldFundsData.Unavailable)
                account == null -> flowOf(ShieldFundsData.Unavailable)
                account.isShieldingAvailable ->
                    messageAvailabilityDataSource.canShowShieldMessage
                        .map { canShowShieldMessage ->
                            when {
                                !canShowShieldMessage -> ShieldFundsData.Unavailable
                                else -> ShieldFundsData.Available(amount = account.transparent.balance)
                            }
                        }

                else -> flowOf(ShieldFundsData.Unavailable)
            }
        }
}

sealed interface ShieldFundsData {
    data class Available(
        val amount: Zatoshi
    ) : ShieldFundsData

    data object Unavailable : ShieldFundsData
}
