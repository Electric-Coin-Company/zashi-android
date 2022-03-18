package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage
import kotlinx.coroutines.runBlocking

object Zip321UriBuildFixture {
    const val URI: String = "zcash:ztestsapling10yy2ex5dcqkclhc7z7yrnjq2z6feyjad56ptwlfgmy77dmaqqrl9" +
        "gyhprdx59qgmsnyfska2kez?amount=1&memo=VGhpcyBpcyBhIHNpbXBsZSBtZW1vLg&message=Thank%20you%20" +
        "for%20your%20purchase"

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MESSAGE = ZecRequestMessage("Thanks for lunch")
    val ADDRESS: WalletAddress.Unified = runBlocking {
        WalletAddress.Unified.new(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }
    val REQUEST = ZecRequest(ADDRESS, AMOUNT, MESSAGE)

    @Suppress("UNUSED_PARAMETER")
    fun new(request: ZecRequest = REQUEST) = URI
}
