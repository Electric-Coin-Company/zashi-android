package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper

internal object SendArgumentsWrapperFixture {
    val RECIPIENT_ADDRESS = WalletFixture.Alice.getAddresses(ZcashNetwork.Testnet).unified
    val MEMO = MemoFixture.new("Thanks for lunch").value
    val AMOUNT = ZatoshiFixture.new(1)
    fun amountToFixtureZecString(amount: Zatoshi?) = amount?.toZecString()

    fun new(
        recipientAddress: String? = RECIPIENT_ADDRESS,
        amount: Zatoshi? = AMOUNT,
        memo: String? = MEMO
    ) = SendArgumentsWrapper(
        recipientAddress = recipientAddress,
        amount = amountToFixtureZecString(amount),
        memo = memo
    )
}
