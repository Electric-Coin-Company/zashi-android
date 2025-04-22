package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.datasource.ShieldFundsAvailability
import co.electriccoin.zcash.ui.common.datasource.ShieldFundsDataSource
import co.electriccoin.zcash.ui.common.datasource.ShieldFundsLockoutDuration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

interface ShieldFundsRepository {
    val availability: Flow<ShieldFundsData>

    suspend fun remindMeLater()
}

class ShieldFundsRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val shieldFundsDataSource: ShieldFundsDataSource,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
) : ShieldFundsRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val availability: Flow<ShieldFundsData> =
        accountDataSource
            .selectedAccount
            .flatMapLatest { account ->
                when {
                    account == null -> flowOf(ShieldFundsData.Unavailable)

                    account.isShieldingAvailable ->
                        combine(
                            messageAvailabilityDataSource.canShowShieldMessage,
                            shieldFundsDataSource.observe(account.sdkAccount.accountUuid)
                        ) { canShowShieldMessage, availability ->
                            when {
                                !canShowShieldMessage -> ShieldFundsData.Unavailable
                                availability is ShieldFundsAvailability.Available ->
                                    ShieldFundsData.Available(
                                        lockoutDuration = availability.lockoutDuration,
                                        amount = account.transparent.balance
                                    )

                                else -> ShieldFundsData.Unavailable
                            }
                        }

                    else -> flowOf(ShieldFundsData.Unavailable)
                }
            }

    override suspend fun remindMeLater() {
        shieldFundsDataSource.remindMeLater(
            forAccount = accountDataSource.getSelectedAccount().sdkAccount.accountUuid
        )
    }
}

sealed interface ShieldFundsData {
    data class Available(
        val lockoutDuration: ShieldFundsLockoutDuration,
        val amount: Zatoshi
    ) : ShieldFundsData

    data object Unavailable : ShieldFundsData
}
