package co.electriccoin.zcash.ui.screen.account.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.TransactionOutput
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TrxItemState

object TransactionUiFixture {
    val OVERVIEW: TransactionOverview = TransactionOverviewFixture.new()

    val RECIPIENT: TransactionRecipient =
        TransactionRecipient.Address(
            WalletFixture.Alice.getAddresses(ZcashNetwork.Mainnet).sapling
        )

    val RECIPIENT_ADDRESS_TYPE: AddressType = AddressType.Shielded

    val EXPANDABLE_STATE: TrxItemState = TrxItemState.COLLAPSED

    val MESSAGES: List<String> = listOf("Thanks for the coffee", "It was great to meet you!")

    val ADDRESS_BOOK_CONTACT: AddressBookContact? = null

    val OUTPUTS: List<TransactionOutput> = emptyList()

    @Suppress("LongParameterList")
    internal fun new(
        overview: TransactionOverview = OVERVIEW,
        recipient: TransactionRecipient = RECIPIENT,
        recipientAddressType: AddressType = RECIPIENT_ADDRESS_TYPE,
        expandableState: TrxItemState = EXPANDABLE_STATE,
        messages: List<String> = MESSAGES,
        addressBookContact: AddressBookContact? = ADDRESS_BOOK_CONTACT,
        outputs: List<TransactionOutput> = OUTPUTS
    ) = TransactionUi(
        overview = overview,
        recipient = recipient,
        recipientAddressType = recipientAddressType,
        expandableState = expandableState,
        messages = messages,
        addressBookContact = addressBookContact,
        outputs = outputs
    )
}
