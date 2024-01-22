package cash.z.ecc.sdk.type

import android.content.Context
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ext.R

/**
 * This class provides Zcash currency types with a distinction mechanism. There is the ZEC currency type, which applies
 * to the Mainnet network type, and TAZ, which applies to the Testnet network type. The currencies can't be mismatched
 * and are tightly connected to their network type.
 *
 * To get some TAZs to your test wallet, visit Testnet faucet: https://faucet.zecpages.com/
 */
sealed class ZcashCurrency(
    val id: Int = 0,
    val name: String,
    val network: ZcashNetwork = ZcashNetwork.Testnet
) {
    object TAZ : ZcashCurrency(id = 0, name = "TAZ", network = ZcashNetwork.Testnet) {
        override fun localizedName(context: Context) = context.getString(R.string.zcash_token_taz)
    }

    object ZEC : ZcashCurrency(id = 1, name = "ZEC", network = ZcashNetwork.Mainnet) {
        override fun localizedName(context: Context) = context.getString(R.string.zcash_token_zec)
    }

    abstract fun localizedName(context: Context): String

    override fun toString(): String = "ZcashCurrency: id=$id, name=$name, network:$network"

    companion object {
        fun fromResources(context: Context) =
            when (ZcashNetwork.fromResources(context)) {
                ZcashNetwork.Mainnet -> ZEC
                ZcashNetwork.Testnet -> TAZ
                else -> error("Not supported ZcashNetwork type while getting ZcashCurrency type.")
            }

        fun getLocalizedName(context: Context): String = fromResources(context).localizedName(context)
    }
}
