package cash.z.ecc.sdk.model

/**
 *
 */
@JvmInline
value class Zatoshi(val value: Long) {
    init {
        require(value >= 0)
    }

    companion object
}
