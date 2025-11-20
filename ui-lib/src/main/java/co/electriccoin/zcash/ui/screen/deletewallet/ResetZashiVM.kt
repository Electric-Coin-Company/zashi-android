package co.electriccoin.zcash.ui.screen.deletewallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ResetZashiVM(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    private val isKeepFilesChecked = MutableStateFlow(true)

    val state: StateFlow<ResetZashiState> =
        isKeepFilesChecked
            .map {
                createState(
                    isKeepFilesChecked = it
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue =
                    createState(
                        isKeepFilesChecked = isKeepFilesChecked.value,
                    )
            )

    private fun createState(isKeepFilesChecked: Boolean) =
        ResetZashiState(
            onBack = { navigationRouter.back() },
            checkboxState =
                CheckboxState(
                    title = stringRes(R.string.delete_wallet_checkbox_title),
                    subtitle = stringRes(R.string.delete_wallet_checkbox_warning_checked),
                    isChecked = isKeepFilesChecked,
                    onClick = ::onCheckboxToggled
                ),
            buttonState =
                ButtonState(
                    text = stringRes(R.string.delete_wallet_button),
                    onClick = ::onConfirmClicked
                ),
        )

    private fun onCheckboxToggled() = isKeepFilesChecked.update { !it }

    private fun onConfirmClicked() =
        navigationRouter.forward(ResetZashiConfirmationArgs(isKeepFilesChecked.value))
}
