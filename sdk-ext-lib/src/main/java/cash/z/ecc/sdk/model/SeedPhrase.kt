package cash.z.ecc.sdk.model

// Consider using ImmutableList here
data class SeedPhrase(val split: List<String>) {
    init {
        require(SEED_PHRASE_SIZE == split.size) {
            "Seed phrase must split into $SEED_PHRASE_SIZE words but was ${split.size}"
        }
    }

    // For security, intentionally override the toString method to reduce risk of accidentally logging secrets
    override fun toString() = "SeedPhrase"

    fun joinToString() = split.joinToString(DEFAULT_DELIMITER)

    fun toByteArray() = joinToString().encodeToByteArray()

    companion object {
        const val SEED_PHRASE_SIZE = 24

        const val DEFAULT_DELIMITER = " "

        fun new(phrase: String) = SeedPhrase(phrase.split(DEFAULT_DELIMITER))
    }
}
