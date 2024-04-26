@file:Suppress("ktlint:standard:filename", "MagicNumber")

package cash.z.ecc.sdk.extension

import co.electriccoin.lightwallet.client.model.LightWalletEndpoint

/*
 * Used for testing purposes only
 */

val LightWalletEndpoint.Companion.Mainnet
    get() =
        LightWalletEndpoint(
            "zec.rocks",
            443,
            isSecure = true
        )

val LightWalletEndpoint.Companion.Testnet
    get() =
        LightWalletEndpoint(
            "lightwalletd.testnet.electriccoin.co",
            9067,
            isSecure = true
        )

const val MIN_PORT_NUMBER = 1
const val MAX_PORT_NUMBER = 65535

fun LightWalletEndpoint.isValid() = host.isNotBlank() && port in MIN_PORT_NUMBER..MAX_PORT_NUMBER
