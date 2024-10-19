package co.electriccoin.zcash.ui.screen.paymentrequest

import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.screen.paymentrequest.model.PaymentRequestArguments

internal object PaymentRequestArgumentsFixture {

    fun new() = PaymentRequestArguments(
        address =
        SerializableAddress(WalletFixture.Alice.getAddresses(ZcashNetwork.Mainnet).unified, AddressType.Unified),
        amount = 10000000,
        memo = "For the coffee",
        proposal = byteArrayOf(),
        zip321Uri = "zcash:t1duiEGg7b39nfQee3XaTY4f5McqfyJKhBi?amount=1&memo=VGhpcyBpcyBhIHNpbXBsZSBt",
    )
}