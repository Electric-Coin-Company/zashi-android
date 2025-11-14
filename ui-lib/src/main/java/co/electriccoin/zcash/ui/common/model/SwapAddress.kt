package co.electriccoin.zcash.ui.common.model

sealed interface SwapAddress {
    val address: String
}

sealed interface ZcashSwapAddress : SwapAddress

@JvmInline
value class ZcashShieldedSwapAddress(
    override val address: String
) : ZcashSwapAddress

@JvmInline
value class ZcashTransparentSwapAddress(
    override val address: String
) : ZcashSwapAddress

@JvmInline
value class DynamicSwapAddress(
    override val address: String
) : SwapAddress
