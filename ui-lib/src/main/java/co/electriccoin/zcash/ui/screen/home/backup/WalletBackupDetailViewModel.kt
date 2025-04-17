package co.electriccoin.zcash.ui.screen.home.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.NavigateToWalletBackupUseCase
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletBackupDetailViewModel(
    private val args: WalletBackupDetail,
    private val navigationRouter: NavigationRouter,
    private val navigateToWalletBackup: NavigateToWalletBackupUseCase
) : ViewModel() {
    val state = MutableStateFlow(
        WalletBackupDetailState(
            onBack = ::onBack,
            onNextClick = ::onNextClick,
            onInfoClick = ::onInfoClick
        )
    ).asStateFlow()

    private fun onNextClick() =
        viewModelScope.launch {
            navigateToWalletBackup(true)
        }

    private fun onInfoClick() {
        navigationRouter.forward(SeedInfo)
    }

    private fun onBack() {
        if (args.isOpenedFromSeedBackupInfo) {
            navigationRouter.replace(SeedBackupInfo)
        } else {
            navigationRouter.back()
        }
    }
}


