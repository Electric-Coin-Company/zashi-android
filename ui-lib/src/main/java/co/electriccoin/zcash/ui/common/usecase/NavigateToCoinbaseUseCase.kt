package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.screen.ExternalUrl

class NavigateToCoinbaseUseCase(
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter
) {
    suspend operator fun invoke() {
        val transparent = accountDataSource.getSelectedAccount().transparent
        val url = getUrl(transparent.address.address)
        navigationRouter.replace(ExternalUrl(url))
    }

    private fun getUrl(address: String): String {
        val appId = BuildConfig.ZCASH_COINBASE_APP_ID
        return "https://pay.coinbase.com/buy/select-asset?appId=$appId&addresses={\"${address}\":[\"zcash\"]}"
    }
}
