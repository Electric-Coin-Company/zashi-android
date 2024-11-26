package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationExpandedInfoState
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationState

object SendConfirmationStateFixture {
    fun new() = SendConfirmationState(
        from = SendConfirmationExpandedInfoState(
            stringRes("Sending from"),
            R.drawable.ic_item_keystone,
            stringRes("Keystone wallet")
        )
    )
}