package co.electriccoin.zcash.ui.screen.restore.height

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.RestoreWalletUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.date.RestoreBDDate
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RestoreBDHeightViewModel(
    private val restoreBDHeight: RestoreBDHeight,
    private val navigationRouter: NavigationRouter,
    private val context: Context,
    private val restoreWallet: RestoreWalletUseCase
) : ViewModel() {
    private val blockHeightText = MutableStateFlow("")

    val state: StateFlow<RestoreBDHeightState> =
        blockHeightText
            .map { text ->
                createState(text)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(blockHeightText.value)
            )

    private fun createState(blockHeight: String): RestoreBDHeightState {
        val isHigherThanSaplingActivationHeight =
            blockHeight
                .toLongOrNull()
                ?.let { it >= ZcashNetwork.fromResources(context).saplingActivationHeight.value } ?: false
        val isValid = blockHeight.isEmpty() || isHigherThanSaplingActivationHeight

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
                    isEnabled = isValid
                ),
            estimate = ButtonState(stringRes(R.string.restore_bd_height_btn), onClick = ::onEstimateClick),
            blockHeight =
                TextFieldState(
                    value = stringRes(blockHeight),
                    onValueChange = ::onValueChanged,
                    error = stringRes("").takeIf { !isValid }
                )
        )
    }

    private fun onEstimateClick() {
        navigationRouter.forward(RestoreBDDate(seed = restoreBDHeight.seed))
    }

    private fun onRestoreClick() {
        restoreWallet(
            seedPhrase = SeedPhrase.new(restoreBDHeight.seed.trim()),
            birthday = blockHeightText.value.toLongOrNull()?.let { BlockHeight.new(it) }
        )
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        navigationRouter.forward(SeedInfo)
    }

    private fun onValueChanged(string: String) {
        blockHeightText.update { string }
    }
}
