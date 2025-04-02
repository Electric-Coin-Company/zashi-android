package co.electriccoin.zcash.ui.screen.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

class BalanceViewModel(
    accountDataSource: AccountDataSource,
    exchangeRateRepository: ExchangeRateRepository,
) : ViewModel() {
    val state: StateFlow<BalanceState> =
        combine(
            accountDataSource.selectedAccount.filterNotNull(),
            exchangeRateRepository.state,
        ) { account, exchangeRateUsd ->
            createState(account, exchangeRateUsd)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            createState(
                account = accountDataSource.allAccounts.value?.firstOrNull { it.isSelected },
                exchangeRateUsd = exchangeRateRepository.state.value
            )
        )

    private fun createState(
        account: WalletAccount?,
        exchangeRateUsd: ExchangeRateState
    ): BalanceState =
        when {
            (
                account != null &&
                    account.spendableBalance.value == 0L &&
                    account.totalBalance.value > 0L &&
                    (account.hasChangePending || account.hasValuePending)
            ) -> {
                BalanceState.Loading(
                    totalBalance = account.totalBalance,
                    spendableBalance = account.spendableBalance,
                    exchangeRate = exchangeRateUsd,
                )
            }

            else -> {
                BalanceState.Available(
                    totalBalance = account?.totalBalance ?: Zatoshi(0),
                    spendableBalance = account?.spendableBalance ?: Zatoshi(0),
                    exchangeRate = exchangeRateUsd,
                )
            }
        }
}
