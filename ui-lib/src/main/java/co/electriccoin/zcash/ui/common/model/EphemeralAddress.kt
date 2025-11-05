package co.electriccoin.zcash.ui.common.model

data class EphemeralAddress(
    val address: String,
    val gapPosition: UInt,
    val gapLimit: UInt,
)
