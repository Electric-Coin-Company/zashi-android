package cash.z.ecc.sdk.model

data class SeedPhrase(val phrase: String) {
    val split = phrase.split(" ")

    init {
        require(SEED_PHRASE_SIZE == split.size) {
            "Seed phrase must split into $SEED_PHRASE_SIZE words but was ${split.size}"
        }
    }

    override fun toString(): String {
        // For security, intentionally override the toString method to reduce risk of accidentally logging secrets
        return "SeedPhrase"
    }

    companion object {
        const val SEED_PHRASE_SIZE = 24
    }
}
