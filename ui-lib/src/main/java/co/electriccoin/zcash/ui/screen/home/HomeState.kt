package co.electriccoin.zcash.ui.screen.home

import co.electriccoin.zcash.ui.design.component.BigIconButtonState

data class HomeState(
    val receiveButton: BigIconButtonState,
    val sendButton: BigIconButtonState,
    val scanButton: BigIconButtonState,
    val moreButton: BigIconButtonState,
)
