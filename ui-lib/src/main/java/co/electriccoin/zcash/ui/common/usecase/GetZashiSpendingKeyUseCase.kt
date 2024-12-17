package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.ZashiSpendingKeyDataSource

class GetZashiSpendingKeyUseCase(
    private val spendingKeyDataSource: ZashiSpendingKeyDataSource,
) {
    suspend operator fun invoke() = spendingKeyDataSource.getZashiSpendingKey()
}
