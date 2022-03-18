package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage

object Zip321UriParseFixture {
    const val URI: String = "zcash:ztestsapling10yy2ex5dcqkclhc7z7yrnjq2z6feyjad56ptwlfgmy77dmaqqrl9" +
        "gyhprdx59qgmsnyfska2kez?amount=1&memo=VGhpcyBpcyBhIHNpbXBsZSBtZW1vLg&message=Thank%20you%20" +
        "for%20your%20purchase"

    const val ADDRESS: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING
    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MESSAGE = ZecRequestMessage("Thanks for lunch")

    @Suppress("UNUSED_PARAMETER")
    suspend fun new(
        toParse: String = URI,
    ) = ZecRequest(WalletAddress.Unified.new(ADDRESS), AMOUNT, MESSAGE)
}
