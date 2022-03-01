package cash.z.ecc.sdk.fixture

import cash.z.ecc.sdk.model.Memo
import cash.z.ecc.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.Zatoshi
import cash.z.ecc.sdk.model.ZecSend

object ZecSendFixture {
    const val ADDRESS: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MEMO = MemoFixture.new()

    suspend fun new(
        address: String = ADDRESS,
        amount: Zatoshi = AMOUNT,
        message: Memo = MEMO
    ) = ZecSend(WalletAddress.Unified.new(address), amount, message)
}
