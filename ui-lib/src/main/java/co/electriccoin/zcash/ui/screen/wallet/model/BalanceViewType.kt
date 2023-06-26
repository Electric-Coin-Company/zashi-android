package co.electriccoin.zcash.ui.screen.wallet.model

sealed interface BalanceViewType {
    object SWIPE: BalanceViewType
    object TOTAL: BalanceViewType
    object SHIELDED: BalanceViewType
    object TRANSPARENT: BalanceViewType

    companion object {
        const val TOTAL_VIEWS = 4
        fun getBalanceViewType(pos: Int): BalanceViewType {
            return when (pos) {
                1 -> TOTAL
                2 -> SHIELDED
                3 -> TRANSPARENT
                else -> SWIPE
            }
        }
    }
}