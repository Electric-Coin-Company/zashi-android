package co.electriccoin.zcash.ui.screen.chooseserver

import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.extension.Testnet
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import kotlinx.collections.immutable.toImmutableList

// TODO [#1273]: Add ChooseServer Tests #1273
// TODO [#1273]: https://github.com/Electric-Coin-Company/zashi-android/issues/1273

@Suppress("UnusedPrivateProperty")
object AvailableServerProvider {
    private const val ZR_HOST = "zec.rocks" // NON-NLS
    private const val ZR_HOST_NA = "na.zec.rocks" // NON-NLS
    private const val ZR_HOST_SA = "sa.zec.rocks" // NON-NLS
    private const val ZR_HOST_EU = "eu.zec.rocks" // NON-NLS
    private const val ZR_HOST_AP = "ap.zec.rocks" // NON-NLS
    private const val ZR_PORT = 443

    private const val YW_HOST_1 = "lwd1.zcash-infra.com" // NON-NLS
    private const val YW_HOST_2 = "lwd2.zcash-infra.com" // NON-NLS
    private const val YW_HOST_3 = "lwd3.zcash-infra.com" // NON-NLS
    private const val YW_HOST_4 = "lwd4.zcash-infra.com" // NON-NLS
    private const val YW_HOST_5 = "lwd5.zcash-infra.com" // NON-NLS
    private const val YW_HOST_6 = "lwd6.zcash-infra.com" // NON-NLS
    private const val YW_HOST_7 = "lwd7.zcash-infra.com" // NON-NLS
    private const val YW_HOST_8 = "lwd8.zcash-infra.com" // NON-NLS
    private const val YW_PORT = 9067

    // NH servers are currently unused and are subject of removal in the future
    private const val NH_HOST_NA = "na.lightwalletd.com" // NON-NLS
    private const val NH_HOST_SA = "sa.lightwalletd.com" // NON-NLS
    private const val NH_HOST_EU = "eu.lightwalletd.com" // NON-NLS
    private const val NH_HOST_AI = "ai.lightwalletd.com" // NON-NLS
    private const val NH_PORT = 443

    fun toList(network: ZcashNetwork) =
        buildList {
            if (network == ZcashNetwork.Mainnet) {
                add(LightWalletEndpoint(ZR_HOST, ZR_PORT, true))

                // Custom server item comes here in the view layer

                add(LightWalletEndpoint(ZR_HOST_NA, ZR_PORT, true))
                add(LightWalletEndpoint(ZR_HOST_SA, ZR_PORT, true))
                add(LightWalletEndpoint(ZR_HOST_EU, ZR_PORT, true))
                add(LightWalletEndpoint(ZR_HOST_AP, ZR_PORT, true))

                add(LightWalletEndpoint(YW_HOST_1, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_2, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_3, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_4, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_5, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_6, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_7, YW_PORT, true))
                add(LightWalletEndpoint(YW_HOST_8, YW_PORT, true))
            } else {
                add(LightWalletEndpoint.Testnet)
            }
        }.toImmutableList()

    fun getDefaultServer(zcashNetwork: ZcashNetwork): LightWalletEndpoint = toList(zcashNetwork).first()
}

// This regex validates server URLs with ports in format: <hostname>:<port>
// While ensuring:
// - Valid hostname format (excluding spaces and special characters)
// - Port numbers within the valid range (1-65535) and without leading zeros
// - Note that this does not cover other URL components like paths or query strings
val regex = "^(([^:/?#\\s]+)://)?([^/?#\\s]+):([1-9][0-9]{3}|[1-5][0-9]{2}|[0-9]{1,2})$".toRegex()

fun validateCustomServerValue(customServer: String): Boolean = regex.matches(customServer)
