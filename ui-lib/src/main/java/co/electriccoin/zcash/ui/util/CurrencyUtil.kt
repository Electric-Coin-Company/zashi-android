package co.electriccoin.zcash.ui.util

import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.zcash.ui.common.model.VersionInfo

val CURRENCY_TICKER: String
    get() =
        when (VersionInfo.NETWORK) {
            ZcashNetwork.Mainnet -> "ZEC"
            ZcashNetwork.Testnet -> "TAZ"
            else -> "ZEC"
        }
