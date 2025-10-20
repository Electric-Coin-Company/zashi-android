package co.electriccoin.zcash.ui.common.model

enum class SwapStatus {
    INCOMPLETE_DEPOSIT,
    PENDING,
    SUCCESS,
    REFUNDED,
    FAILED,
    PROCESSING,
    EXPIRED;

    val isTerminal: Boolean
        get() = this == SUCCESS || this == REFUNDED || this == FAILED || this == EXPIRED
}
