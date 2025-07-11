package co.electriccoin.zcash.ui.screen.tor.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.EnableTorUseCase
import co.electriccoin.zcash.ui.common.usecase.IsTorEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TorSettingsVM(
    isTorEnabled: IsTorEnabledUseCase,
    private val navigationRouter: NavigationRouter,
    private val enableTor: EnableTorUseCase
) : ViewModel() {
    val state =
        isTorEnabled
            .observe()
            .map { isEnabled ->
                TorSettingsState(
                    isOptedIn = isEnabled == true,
                    onSaveClick = ::onOptInClick,
                    onDismiss = ::onBack
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun onBack() = navigationRouter.back()

    private fun onOptInClick(optInt: Boolean) = viewModelScope.launch { enableTor(optInt) }
}
