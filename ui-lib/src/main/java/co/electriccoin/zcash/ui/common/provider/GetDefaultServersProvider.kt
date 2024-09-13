package co.electriccoin.zcash.ui.common.provider

import android.app.Application
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.extension.Testnet
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

// TODO [#1273]: Add ChooseServer Tests #1273
// TODO [#1273]: https://github.com/Electric-Coin-Company/zashi-android/issues/1273
class GetDefaultServersProvider(private val application: Application) {
    private val lightWalletEndpoints by lazy {
        if (ZcashNetwork.fromResources(application) == ZcashNetwork.Mainnet) {
            listOf(
                LightWalletEndpoint(ZR_HOST, ZR_PORT, true),
                LightWalletEndpoint(ZR_HOST_NA, ZR_PORT, true),
                LightWalletEndpoint(ZR_HOST_SA, ZR_PORT, true),
                LightWalletEndpoint(ZR_HOST_EU, ZR_PORT, true),
                LightWalletEndpoint(ZR_HOST_AP, ZR_PORT, true),
                LightWalletEndpoint(YW_HOST_1, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_2, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_3, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_4, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_5, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_6, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_7, YW_PORT, true),
                LightWalletEndpoint(YW_HOST_8, YW_PORT, true),
            )
        } else {
            listOf(LightWalletEndpoint.Testnet)
        }
    }

    operator fun invoke() = lightWalletEndpoints

    fun defaultEndpoint() = this().first()
}

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
