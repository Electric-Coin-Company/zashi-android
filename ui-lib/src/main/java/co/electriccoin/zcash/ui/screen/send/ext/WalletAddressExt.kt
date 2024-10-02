package co.electriccoin.zcash.ui.screen.send.ext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SerializableAddress

/**
 * How far into the address will be abbreviation look forwards and backwards.
 *
 * This value align with ZIP 316 specification.
 */
internal const val ABBREVIATION_INDEX = 20

@Composable
@ReadOnlyComposable
internal fun WalletAddress.abbreviated(): String {
    LocalConfiguration.current
    return abbreviated(LocalContext.current)
}

internal fun WalletAddress.abbreviated(context: Context): String {
    require(address.length >= ABBREVIATION_INDEX) { "Address must be at least 5 characters long" }

    val firstFive = address.substring(0, ABBREVIATION_INDEX)
    val lastFive = address.substring(address.length - ABBREVIATION_INDEX, address.length)

    return context.getString(R.string.send_abbreviated_address_format, firstFive, lastFive)
}

internal fun WalletAddress.toSerializableAddress() =
    SerializableAddress(
        address = address,
        type =
            when (this) {
                is WalletAddress.Unified -> AddressType.Unified
                is WalletAddress.Sapling -> AddressType.Shielded
                is WalletAddress.Transparent -> AddressType.Transparent
                is WalletAddress.Tex -> AddressType.Tex
            }
    )
