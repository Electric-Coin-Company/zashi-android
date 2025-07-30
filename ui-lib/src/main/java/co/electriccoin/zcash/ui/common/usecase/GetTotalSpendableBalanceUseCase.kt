package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTotalSpendableBalanceUseCase(
    private val accountDataSource: AccountDataSource,
) {
    fun observe(): Flow<Zatoshi?> = accountDataSource.selectedAccount.map { it?.spendableShieldedBalance }
}
