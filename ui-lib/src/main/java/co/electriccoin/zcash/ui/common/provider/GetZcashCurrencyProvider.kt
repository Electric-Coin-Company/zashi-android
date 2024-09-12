package co.electriccoin.zcash.ui.common.provider

import android.app.Application
import cash.z.ecc.sdk.type.ZcashCurrency

class GetZcashCurrencyProvider(private val application: Application) {
    operator fun invoke() = ZcashCurrency.fromResources(application)

    fun getLocalizedName() = ZcashCurrency.getLocalizedName(application)
}
