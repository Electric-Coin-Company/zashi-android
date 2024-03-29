package co.electriccoin.zcash.ui.screen.account.fixture

import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.fixture.WalletFixture
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.TransactionRecipient
import cash.z.ecc.android.sdk.model.ZcashNetwork
import co.electriccoin.zcash.ui.screen.account.model.TransactionUi
import co.electriccoin.zcash.ui.screen.account.model.TrxItemState

object TransactionUiFixture {
    val OVERVIEW: TransactionOverview = TransactionOverviewFixture.new()

    val RECIPIENT: TransactionRecipient =
        TransactionRecipient.Address(
            WalletFixture.Alice.getAddresses(ZcashNetwork.Mainnet).sapling
        )

    val EXPANDABLE_STATE: TrxItemState = TrxItemState.COLLAPSED

    val MESSAGES: List<String> = listOf("Thanks for the coffee", "It was great to meet you!")

    internal fun new(
        overview: TransactionOverview = OVERVIEW,
        recipient: TransactionRecipient = RECIPIENT,
        expandableState: TrxItemState = EXPANDABLE_STATE,
        messages: List<String> = MESSAGES,
    ) = TransactionUi(
        overview = overview,
        recipient = recipient,
        expandableState = expandableState,
        messages = messages
    )
}
