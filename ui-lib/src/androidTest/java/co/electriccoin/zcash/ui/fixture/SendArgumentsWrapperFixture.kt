package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper

internal object SendArgumentsWrapperFixture {
    const val RECIPIENT_ADDRESS = "tmEjY6KfCryQhJ1hKSGiA7p8EeVggpvN78r"
    const val MEMO = "Thanks for lunch"
    val AMOUNT = Zatoshi(123)
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
