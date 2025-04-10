package co.electriccoin.zcash.ui.screen.home.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.RemindWalletBackupLaterUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalletBackupInfoViewModel(
    private val navigationRouter: NavigationRouter,
    private val remindWalletBackupLater: RemindWalletBackupLaterUseCase
) : ViewModel() {
    val state: StateFlow<WalletBackupInfoState?> = MutableStateFlow(
        WalletBackupInfoState(
            onBack = ::onBack,
            secondaryButton = ButtonState(
                text = stringRes(R.string.general_remind_me_later),
                onClick = ::onRemindMeLaterClick
            ),
            primaryButton = ButtonState(
                text = stringRes(R.string.general_ok),
                onClick = ::onPrimaryClick
            )
        )
    )

    private fun onPrimaryClick() {
        navigationRouter.replace(WalletBackupDetail(true))
    }

    private fun onRemindMeLaterClick() = viewModelScope.launch { remindWalletBackupLater() }

    private fun onBack() = navigationRouter.back()
}
