package cash.z.ecc.sdk.model

import cash.z.ecc.sdk.fixture.Zip321UriBuildFixture
import cash.z.ecc.sdk.fixture.Zip321UriParseFixture

data class ZecRequest(val address: WalletAddress.Unified, val amount: Zatoshi, val message: ZecRequestMessage) {

    fun toUri(): String {
        return Zip321UriBuildFixture.new(this)
    }

    companion object {
        suspend fun fromUri(uriString: String) {
            Zip321UriParseFixture.new(uriString)
        }
    }
}

@JvmInline
value class ZecRequestMessage(val value: String) {
    init {
        require(value.length <= MAX_MESSAGE_LENGTH)
    }

    companion object {
        // TODO [#219]: Define a maximum message length
        // Also note that the length varies from what the user types in versus the encoded version
        // that is actually sent.
        const val MAX_MESSAGE_LENGTH = 320
    }
}
