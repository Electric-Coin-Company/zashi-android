package co.electriccoin.zcash.ui.common.repository

import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.hasChangePending
import co.electriccoin.zcash.ui.common.model.hasValuePending
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.model.totalBalance
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

interface BalanceRepository {
    /**
     * A flow of the wallet balances state used for the UI layer. It's computed from [WalletSnapshot]'s properties
     * and provides the result [BalanceState] UI state.
     */
    val state: StateFlow<BalanceState>
}

class BalanceRepositoryImpl(
    walletRepository: WalletRepository,
    exchangeRateRepository: ExchangeRateRepository
) : BalanceRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val state: StateFlow<BalanceState> =
        combine(
            walletRepository.walletSnapshot.filterNotNull(),
            exchangeRateRepository.state,
        ) { snapshot, exchangeRateUsd ->
            when {
                // Show the loader only under these conditions:
                // - Available balance is currently zero AND total balance is non-zero
                // - And wallet has some ChangePending or ValuePending in progress
                (
                    snapshot.spendableBalance().value == 0L &&
                        snapshot.totalBalance().value > 0L &&
                        (snapshot.hasChangePending() || snapshot.hasValuePending())
                ) -> {
                    BalanceState.Loading(
                        totalBalance = snapshot.totalBalance(),
                        spendableBalance = snapshot.spendableBalance(),
                        exchangeRate = exchangeRateUsd
                    )
                }

                else -> {
                    BalanceState.Available(
                        totalBalance = snapshot.totalBalance(),
                        spendableBalance = snapshot.spendableBalance(),
                        exchangeRate = exchangeRateUsd
                    )
                }
            }
        }.stateIn(
            scope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            BalanceState.None(ExchangeRateState.OptedOut)
        )
}
