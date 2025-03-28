package co.electriccoin.zcash.ui.screen.home

import co.electriccoin.zcash.ui.design.component.BigIconButtonState

data class HomeState(
    val firstButton: BigIconButtonState,
    val secondButton: BigIconButtonState,
    val thirdButton: BigIconButtonState,
    val fourthButton: BigIconButtonState,
)

data class HomeRestoreDialogState(
    val onClick: () -> Unit
)
