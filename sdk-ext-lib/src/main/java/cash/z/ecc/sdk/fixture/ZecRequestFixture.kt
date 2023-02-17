package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecRequest
import cash.z.ecc.sdk.model.ZecRequestMessage

object ZecRequestFixture {
    const val ADDRESS: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MESSAGE = ZecRequestMessage("Thanks for lunch")

    suspend fun new(
        address: String = ADDRESS,
        amount: Zatoshi = AMOUNT,
        message: ZecRequestMessage = MESSAGE
    ) = ZecRequest(WalletAddress.Unified.new(address), amount, message)
}
