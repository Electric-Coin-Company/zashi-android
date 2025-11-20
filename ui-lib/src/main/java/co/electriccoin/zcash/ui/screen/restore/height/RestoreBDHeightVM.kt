package co.electriccoin.zcash.ui.screen.restore.height

import android.content.Context
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDateArgs
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import co.electriccoin.zcash.ui.screen.restore.tor.RestoreTorArgs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RestoreBDHeightVM(
    private val restoreBDHeight: RestoreBDHeight,
    private val navigationRouter: NavigationRouter,
    private val context: Context,
) : ViewModel() {
    private val blockHeightText = MutableStateFlow(NumberTextFieldInnerState())

    val state: StateFlow<RestoreBDHeightState> =
        blockHeightText
            .map { text ->
                createState(text)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(blockHeightText.value)
            )

    private fun createState(blockHeight: NumberTextFieldInnerState): RestoreBDHeightState {
        val isHigherThanSaplingActivationHeight =
            blockHeight
                .amount
                ?.let { it.toLong() >= ZcashNetwork.fromResources(context).saplingActivationHeight.value } ?: false
        val isValid = !blockHeight.innerTextFieldState.value.isEmpty() && isHigherThanSaplingActivationHeight

        return RestoreBDHeightState(
            onBack = ::onBack,
            dialogButton =
                IconButtonState(
                    icon = R.drawable.ic_help,
                    onClick = ::onInfoButtonClick,
                ),
            restore =
                ButtonState(
                    stringRes(R.string.restore_bd_restore_btn),
                    onClick = ::onRestoreClick,
                    isEnabled = isValid,
                    hapticFeedbackType = HapticFeedbackType.Confirm
                ),
            estimate = ButtonState(stringRes(R.string.restore_bd_height_btn), onClick = ::onEstimateClick),
            blockHeight = NumberTextFieldState(innerState = blockHeight, onValueChange = ::onValueChanged)
        )
    }

    private fun onEstimateClick() {
        navigationRouter.forward(RestoreBDDateArgs(seed = restoreBDHeight.seed))
    }

    private fun onRestoreClick() {
        navigationRouter.forward(
            RestoreTorArgs(
                seed = restoreBDHeight.seed.trim(),
                blockHeight = blockHeightText.value.amount?.toLong() ?: return
            )
        )
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        navigationRouter.forward(SeedInfo)
    }

    private fun onValueChanged(state: NumberTextFieldInnerState) {
        blockHeightText.update { state }
    }
}
