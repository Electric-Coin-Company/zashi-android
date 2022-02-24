package cash.z.ecc.sdk.model

import cash.z.ecc.sdk.fixture.ZecRequestFixture
import kotlinx.coroutines.runBlocking

data class ZecRequest(val address: WalletAddress.Unified, val amount: Zatoshi, val message: ZecRequestMessage) {

    // https://github.com/zcash/zcash-android-wallet-sdk/issues/397
    // TODO [#397]: There's an issue in the SDK to implement the parser
    @Suppress("FunctionOnlyReturningConstant")
    fun toUri(): String = ""

    companion object {

        @Suppress("UNUSED_PARAMETER")
        suspend fun fromUri(uriString: String) {
            // TODO [#397]: Use URI parser
            runBlocking { ZecRequestFixture.new() }
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
