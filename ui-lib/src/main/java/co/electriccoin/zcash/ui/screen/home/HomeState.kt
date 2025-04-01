package co.electriccoin.zcash.ui.screen.home

import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.screen.home.messages.HomeMessageState

data class HomeState(
    val firstButton: BigIconButtonState,
    val secondButton: BigIconButtonState,
    val thirdButton: BigIconButtonState,
    val fourthButton: BigIconButtonState,
    val message: HomeMessageState?
)

data class HomeRestoreDialogState(
    val onClick: () -> Unit
)
