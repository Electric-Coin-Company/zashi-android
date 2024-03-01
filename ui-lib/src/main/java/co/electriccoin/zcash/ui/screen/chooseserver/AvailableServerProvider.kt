package co.electriccoin.zcash.ui.screen.chooseserver

import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.extension.Mainnet
import cash.z.ecc.sdk.extension.Testnet
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import kotlinx.collections.immutable.toImmutableList

// TODO [#1273]: Add ChooseServer Tests #1273
// TODO [#1273]: https://github.com/Electric-Coin-Company/zashi-android/issues/1273

object AvailableServerProvider {
    // North America  | [na.lightwalletd.com](http://na.lightwalletd.com/) | 443
    // South America | [sa.lightwalletd.com](http://sa.lightwalletd.com/) | 443
    // Europe & Africa | [eu.lightwalletd.com](http://eu.lightwalletd.com/) | 443
    // Asia & Oceania | [ai.lightwalletd.com](http://ai.lightwalletd.com/) | 443
    // Plus current network defaults:
    // Mainnet: mainnet.lightwalletd.com | 9067
    // Testnet: lightwalletd.testnet.electriccoin.co | 9067

    private const val HOST_NA = "na.lightwalletd.com" // NON-NLS
    private const val HOST_SA = "sa.lightwalletd.com" // NON-NLS
    private const val HOST_EU = "eu.lightwalletd.com" // NON-NLS
    private const val HOST_AI = "ai.lightwalletd.com" // NON-NLS
    private const val PORT = 443

    fun toList(network: ZcashNetwork) =
        buildList {
            if (network == ZcashNetwork.Mainnet) {
                add(LightWalletEndpoint.Mainnet)
                add(LightWalletEndpoint(HOST_NA, PORT, true))
                add(LightWalletEndpoint(HOST_SA, PORT, true))
                add(LightWalletEndpoint(HOST_EU, PORT, true))
                add(LightWalletEndpoint(HOST_AI, PORT, true))
            } else {
                add(LightWalletEndpoint.Testnet)
            }
        }.toImmutableList()
}
