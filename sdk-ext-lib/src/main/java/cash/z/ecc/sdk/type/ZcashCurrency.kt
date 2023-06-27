package cash.z.ecc.sdk.type

import android.content.Context
import cash.z.ecc.android.sdk.model.ZcashNetwork

/**
 * This class provides Zcash currency types with a distinction mechanism. There is the ZEC currency type, which applies
 * to the Mainnet network type, and TAZ, which applies to the Testnet network type. The currencies can't be mismatched
 * and are tightly connected to their network type.
 *
 * To get some TAZs to your test wallet, visit Testnet faucet: https://faucet.zecpages.com/
 */
sealed class ZcashCurrency(
    val id: Int = 0,
    val name: String = "TAZ",
    val network: ZcashNetwork = ZcashNetwork.Testnet
) {
    object TAZ : ZcashCurrency(id = 0, name = "TAZ", network = ZcashNetwork.Testnet)

    object ZEC : ZcashCurrency(id = 1, name = "ZEC", network = ZcashNetwork.Mainnet)

    override fun toString(): String = "ZcashCurrency: id=$id, name=$name, network:$network"

    companion object {
        fun fromResources(context: Context) =
            when (ZcashNetwork.fromResources(context)) {
                ZcashNetwork.Mainnet ->
                    ZEC
                ZcashNetwork.Testnet ->
                    TAZ
                else ->
                    error("Not supported ZcashNetwork type while getting ZcashCurrency type.")
            }
    }
}
