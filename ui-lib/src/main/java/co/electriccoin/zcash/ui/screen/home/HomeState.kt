package co.electriccoin.zcash.ui.screen.home

import androidx.compose.runtime.Immutable
import co.electriccoin.zcash.ui.design.component.BigIconButtonState

@Immutable
data class HomeState(
    val firstButton: BigIconButtonState,
    val secondButton: BigIconButtonState,
    val thirdButton: BigIconButtonState,
    val fourthButton: BigIconButtonState,
    val message: HomeMessageState?
)

@Immutable
data object HomeRestoreSuccessDialogState
