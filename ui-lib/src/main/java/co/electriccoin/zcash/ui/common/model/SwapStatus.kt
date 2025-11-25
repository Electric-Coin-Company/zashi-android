package co.electriccoin.zcash.ui.common.model

enum class SwapStatus(
    val value: String
) {
    INCOMPLETE_DEPOSIT("INCOMPLETE_DEPOSIT"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    REFUNDED("REFUNDED"),
    FAILED("FAILED"),
    PROCESSING("PROCESSING"),
    EXPIRED("EXPIRED");

    val isTerminal: Boolean
        get() = this == SUCCESS || this == REFUNDED || this == FAILED || this == EXPIRED
}
