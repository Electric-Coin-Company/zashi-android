package co.electriccoin.zcash.ui.util

import co.electriccoin.zcash.ui.common.model.NetworkDimension
import co.electriccoin.zcash.ui.common.model.VersionInfo

val CURRENCY_TICKER: String
    get() =
        when (VersionInfo.NETWORK) {
            NetworkDimension.MAINNET -> "ZEC"
            NetworkDimension.TESTNET -> "TAZ"
        }
