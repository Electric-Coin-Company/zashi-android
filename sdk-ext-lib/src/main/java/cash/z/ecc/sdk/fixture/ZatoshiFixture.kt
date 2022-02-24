package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.Zatoshi

object ZatoshiFixture {
    @Suppress("MagicNumber")
    const val ZATOSHI_LONG = 123456789L

    fun new(value: Long = ZATOSHI_LONG) = Zatoshi(value)
}
