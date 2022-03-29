package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage
import kotlinx.coroutines.runBlocking

object Zip321UriBuildFixture {
    // TODO [#161]: Pending SDK support
    const val URI: String = "zcash:Unified%20GitHub%20Issue%20#161?amount=123&message=Thank%20you%20" +
        "for%20your%20purchase"

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MESSAGE = ZecRequestMessage("Thank you for your purchase")
    val ADDRESS: WalletAddress.Unified = runBlocking {
        WalletAddress.Unified.new(WalletAddressFixture.UNIFIED_ADDRESS_STRING)
    }
    val REQUEST = ZecRequest(ADDRESS, AMOUNT, MESSAGE)

    @Suppress("UNUSED_PARAMETER")
    suspend fun new(request: ZecRequest = REQUEST) = URI
}
