package co.electriccoin.zcash.ui.screen.receive

internal enum class ReceiveAddressType {
    Unified,
    Sapling,
    Transparent;

    companion object {
        fun fromOrdinal(ordinal: Int) = entries[ordinal]
    }
}
