package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage

object Zip321UriParseFixture {
    // TODO [#161]: Pending SDK support
    const val URI: String = "zcash:Unified%20GitHub%20Issue%20#161?amount=123&message=Thank%20you%20" +
        "for%20your%20purchase"

    const val ADDRESS: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MESSAGE = ZecRequestMessage("Thank you for your purchase")

    // TODO [#397]: Waiting for an implementation of Uri parser in SDK project
    // Should return ZecRequest.fromUri(toParse) ideally, but it'd end up with an infinite loop for now.
    @Suppress("UNUSED_PARAMETER")
    suspend fun new(
        toParse: String = URI
    ) = ZecRequest(WalletAddress.Unified.new(ADDRESS), AMOUNT, MESSAGE)
}
