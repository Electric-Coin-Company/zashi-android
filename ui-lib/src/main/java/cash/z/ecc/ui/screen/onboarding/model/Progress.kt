package cash.z.ecc.ui.screen.onboarding.model

data class Progress(val current: Index, val last: Index) {
    init {
        require(last.value > 0) { "last must be > 0 but was $last" }
        require(last.value >= current.value) { "last ($last) must be >= current ($current)" }
    }

    fun percent() = PercentDecimal((current.value + 1).toFloat() / (last.value + 1).toFloat())

    companion object {
        val EMPTY = Progress(Index(0), Index(1))
    }
}
