package co.electriccoin.zcash.ui.screen.restore.date

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.RestoreSeedDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RestoreBDDateViewModel(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    private val isDialogVisible = MutableStateFlow(false)

    val dialogState =
        isDialogVisible
            .map { isDialogVisible ->
                RestoreSeedDialogState(
                    ::onCloseDialogClick
                ).takeIf { isDialogVisible }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val state: StateFlow<RestoreBDDateState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        RestoreBDDateState(
            next = ButtonState(stringRes(R.string.restore_bd_height_btn), onClick = ::onEstimateClick),
            dialogButton = IconButtonState(icon = R.drawable.ic_info, onClick = ::onInfoButtonClick),
            onBack = ::onBack,
        )

    private fun onEstimateClick() {
        // do nothing
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        isDialogVisible.update { true }
    }

    private fun onCloseDialogClick() {
        isDialogVisible.update { false }
    }
}
