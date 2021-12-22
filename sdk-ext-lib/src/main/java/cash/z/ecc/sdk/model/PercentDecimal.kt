package cash.z.ecc.sdk.model

/**
 * @param decimal A percent represented as a `Double` decimal value in the range of [0, 1].
 */
@JvmInline
value class PercentDecimal(val decimal: Float) {
    init {
        require(decimal >= MIN)
        require(decimal <= MAX)
    }

    companion object {
        const val MIN = 0.0f
        const val MAX = 1.0f
        val ZERO_PERCENT = PercentDecimal(MIN)
        val ONE_HUNDRED_PERCENT = PercentDecimal(MAX)

        fun newLenient(decimal: Float) = PercentDecimal(
            decimal
                .coerceAtLeast(MIN)
                .coerceAtMost(MAX)
        )
    }
}
