package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.datasource.AccountDataSource

class GetTransparentAddressUseCase(
    private val accountDataSource: AccountDataSource,
) {
    suspend operator fun invoke() = accountDataSource.getSelectedAccount().transparent
}
