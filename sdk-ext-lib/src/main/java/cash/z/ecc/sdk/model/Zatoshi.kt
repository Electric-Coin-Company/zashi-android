package cash.z.ecc.sdk.model

/**
 *
 */
@JvmInline
value class Zatoshi(val amount: Long) {
    init {
        require(amount >= 0)
    }

    companion object
}
