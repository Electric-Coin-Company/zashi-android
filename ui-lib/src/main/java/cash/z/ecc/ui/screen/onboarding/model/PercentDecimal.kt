package cash.z.ecc.ui.screen.onboarding.model

/**
 * @param decimal A percent represented as a `Double` decimal value in the range of [0, 1].
 */
@JvmInline
value class PercentDecimal(val decimal: Float) {
    init {
        require(EXPECTED_RANGE.contains(decimal)) { "$decimal is outside of range $EXPECTED_RANGE" }
    }

    companion object {
        private val EXPECTED_RANGE = 0.0f..1.0f
    }
}
