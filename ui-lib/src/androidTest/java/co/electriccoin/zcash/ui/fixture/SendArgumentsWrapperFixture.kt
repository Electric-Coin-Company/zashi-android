package co.electriccoin.zcash.ui.fixture

import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.SerializableAddress
import co.electriccoin.zcash.ui.screen.send.Send

internal object SendArgumentsWrapperFixture {
    val RECIPIENT_ADDRESS =
        SerializableAddress(
            address = WalletFixture.Alice.getAddresses(ZcashNetwork.Testnet).unified,
            type = AddressType.Unified
        )

    fun new(recipientAddress: SerializableAddress? = RECIPIENT_ADDRESS) =
        Send(
            recipientAddress = recipientAddress?.toRecipient(),
        )
}
