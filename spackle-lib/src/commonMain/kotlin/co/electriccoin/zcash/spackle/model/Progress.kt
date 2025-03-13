package co.electriccoin.zcash.spackle.model

data class Progress(
    val current: Index,
    val last: Index
) {
    init {
        require(last.value > 0) { "last must be > 0 but was $last" }
        require(last.value >= current.value) { "last ($last) must be >= current ($current)" }
    }

    companion object {
        val EMPTY = Progress(Index(0), Index(1))
    }
}
