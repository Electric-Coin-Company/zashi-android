package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString

// Eventually, this could move into the SDK and provide a stronger API for amounts
@JvmInline
value class Zatoshi(val amount: Long) {
    init {
        require(amount >= 0)
    }

    override fun toString() = amount.convertZatoshiToZecString(DECIMALS, DECIMALS)

    companion object {
        private const val DECIMALS = 8
    }
}
