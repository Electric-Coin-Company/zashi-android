package co.electriccoin.zcash.ui.screen.send.ext

import android.content.Context
import cash.z.ecc.android.sdk.model.WalletAddress

/**
 * How far into the address will be abbreviation look forwards and backwards.
 *
 * This value align with ZIP 316 specification.
 */
internal const val ABBREVIATION_INDEX = 20

internal fun WalletAddress.abbreviated(context: Context): String {
    require(address.length >= ABBREVIATION_INDEX) { "Address must be at least 5 characters long" }

    return buildString {
        append(address.substring(0, ABBREVIATION_INDEX))
        append(context.getString(co.electriccoin.zcash.ui.design.R.string.triple_dots))
    }
}
