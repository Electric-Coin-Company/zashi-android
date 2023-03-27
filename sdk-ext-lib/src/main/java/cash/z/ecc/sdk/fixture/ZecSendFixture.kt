package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend

object ZecSendFixture {
    const val ADDRESS: String = WalletAddressFixture.UNIFIED_ADDRESS_STRING

    @Suppress("MagicNumber")
    val AMOUNT = Zatoshi(123)
    val MEMO = MemoFixture.new()

    suspend fun new(
        address: String = ADDRESS,
        amount: Zatoshi = AMOUNT,
        memo: Memo = MEMO
    ) = ZecSend(WalletAddress.Unified.new(address), amount, memo)
}
