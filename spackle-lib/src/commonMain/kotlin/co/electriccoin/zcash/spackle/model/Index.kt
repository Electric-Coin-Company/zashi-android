package co.electriccoin.zcash.spackle.model

/**
 * Useful for accessing arrays or lists by index.
 *
 * @param value A 0-based index.  Must be >= 0
 */
@JvmInline
value class Index(
    val value: Int
) {
    init {
        require(value >= 0) { "Index must be >= 0 but actually is $value" }
    }
}
