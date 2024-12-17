package co.electriccoin.zcash.ui.screen.sendconfirmation.model

import co.electriccoin.zcash.ui.design.util.StringResource

data class SendConfirmationState(
    val from: SendConfirmationExpandedInfoState?
)

data class SendConfirmationExpandedInfoState(
    val title: StringResource,
    val icon: Int,
    val text: StringResource
)
