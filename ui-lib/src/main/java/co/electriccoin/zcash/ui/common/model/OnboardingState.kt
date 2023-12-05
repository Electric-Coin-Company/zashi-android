package co.electriccoin.zcash.ui.common.model

/**
 * Common Onboarding/SecurityWarning/Recovery screen enum.
 *
 * WARN: Do NOT re-order or change the values, it would update their ordinal numbers, and thus break screens ordering.
 */
enum class OnboardingState {
    NONE,
    NEEDS_WARN,
    NEEDS_BACKUP,
    READY;

    fun toNumber() = ordinal

    companion object {
        fun fromNumber(ordinal: Int) = entries[ordinal]
    }
}
