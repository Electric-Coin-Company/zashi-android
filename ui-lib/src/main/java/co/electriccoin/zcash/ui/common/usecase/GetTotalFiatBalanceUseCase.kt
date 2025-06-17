package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.ext.convertZatoshiToZec
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.MathContext

class GetTotalFiatBalanceUseCase(
    private val accountDataSource: AccountDataSource,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    operator fun invoke() =
        calculate(
            accountDataSource.allAccounts.value?.firstOrNull { it.isSelected },
            exchangeRateRepository.state.value
        )

    fun observe(): Flow<BigDecimal?> =
        combine(
            accountDataSource.selectedAccount,
            exchangeRateRepository.state
        ) { account, exchangeRate ->
            calculate(account, exchangeRate)
        }

    private fun calculate(
        account: WalletAccount?,
        exchangeRate: ExchangeRateState,
    ) = if (account == null || exchangeRate !is ExchangeRateState.Data || exchangeRate.currencyConversion == null) {
        null
    } else {
        account.totalBalance
            .convertZatoshiToZec()
            .multiply(BigDecimal(exchangeRate.currencyConversion.priceOfZec), MathContext.DECIMAL128)
    }
}
