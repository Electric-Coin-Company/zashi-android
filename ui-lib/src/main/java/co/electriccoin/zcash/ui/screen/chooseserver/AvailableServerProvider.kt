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

    private const val NH_HOST_NA = "na.lightwalletd.com" // NON-NLS
    private const val NH_HOST_SA = "sa.lightwalletd.com" // NON-NLS
    private const val NH_HOST_EU = "eu.lightwalletd.com" // NON-NLS
    private const val NH_HOST_AI = "ai.lightwalletd.com" // NON-NLS
    private const val NH_PORT = 443

    private const val YW_HOST_1 = "lwd1.zcash-infra.com" // NON-NLS
    private const val YW_HOST_2 = "lwd2.zcash-infra.com" // NON-NLS
    private const val YW_HOST_3 = "lwd3.zcash-infra.com" // NON-NLS
    private const val YW_HOST_4 = "lwd4.zcash-infra.com" // NON-NLS
    private const val YW_HOST_5 = "lwd5.zcash-infra.com" // NON-NLS
    private const val YW_HOST_6 = "lwd6.zcash-infra.com" // NON-NLS
    private const val YW_HOST_7 = "lwd7.zcash-infra.com" // NON-NLS
    private const val YW_HOST_8 = "lwd8.zcash-infra.com" // NON-NLS
    private const val YW_PORT = 9067

    fun toList(network: ZcashNetwork) =
        buildList {
            if (network == ZcashNetwork.Mainnet) {
                add(LightWalletEndpoint.Mainnet)

                add(LightWalletEndpoint(YW_HOST_1, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_2, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_3, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_4, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_5, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_6, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_7, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_8, YW_PORT, true))

                add(LightWalletEndpoint(NH_HOST_NA, NH_PORT, true))
                add(LightWalletEndpoint(NH_HOST_SA, NH_PORT, true))
                add(LightWalletEndpoint(NH_HOST_EU, NH_PORT, true))
                add(LightWalletEndpoint(NH_HOST_AI, NH_PORT, true))
            } else {
                add(LightWalletEndpoint.Testnet)
            }
        }.toImmutableList()
}

// This regex validates server URLs with ports in format: <hostname>:<port>
// While ensuring:
// - Valid hostname format (excluding spaces and special characters)
// - Port numbers within the valid range (1-65535) and without leading zeros
// - Note that this does not cover other URL components like paths or query strings
val regex = "^(([^:/?#\\s]+)://)?([^/?#\\s]+):([1-9][0-9]{3}|[1-5][0-9]{2}|[0-9]{1,2})$".toRegex()

fun validateCustomServerValue(customServer: String): Boolean = regex.matches(customServer)
