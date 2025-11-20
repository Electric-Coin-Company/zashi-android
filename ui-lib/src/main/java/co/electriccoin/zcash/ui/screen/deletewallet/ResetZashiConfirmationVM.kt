package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResetZashiConfirmationVM(
    private val args: ResetZashiConfirmationArgs,
    private val resetZashi: ResetZashiUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<ResetZashiConfirmationState?> =
        MutableStateFlow(createBottomSheetState())
            .asStateFlow()

    private var resetJob: Job? = null

    private fun createBottomSheetState(): ResetZashiConfirmationState =
        ResetZashiConfirmationState(
            onBack = ::onDismissBottomSheet,
            onConfirm = ::onConfirmCLick,
            onCancel = ::onDismissBottomSheet
        )

    private fun onDismissBottomSheet() = navigationRouter.back()

    private fun onConfirmCLick() {
        if (resetJob?.isActive == true) return
        resetJob = viewModelScope.launch { resetZashi(keepFiles = args.keepFiles) }
    }
}
