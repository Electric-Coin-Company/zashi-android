package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.IsExchangeRateEnabledStorageProvider
import co.electriccoin.zcash.ui.common.provider.IsTorEnabledStorageProvider
import co.electriccoin.zcash.ui.common.usecase.OptInExchangeRateUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ExchangeRateSettingsVM(
    isTorEnabledStorageProvider: IsTorEnabledStorageProvider,
    private val navigationRouter: NavigationRouter,
    private val optInExchangeRate: OptInExchangeRateUseCase,
    private val isExchangeRateEnabledStorageProvider: IsExchangeRateEnabledStorageProvider,
) : ViewModel() {
    private var isOptedInOriginal = false

    private val isOptedIn = MutableStateFlow(false)

    val state =
        combine(
            isOptedIn,
            isTorEnabledStorageProvider.observe()
        ) { isOptedIn, isTorEnabled ->
            createState(isOptedIn, isTorEnabled)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(isOptedIn.value, false)
        )

    init {
        viewModelScope.launch {
            isOptedInOriginal = isExchangeRateEnabledStorageProvider.get() == true
            isOptedIn.update { isOptedInOriginal }
        }
    }

    private fun createState(isOptedIn: Boolean, isTorEnabled: Boolean?) =
        ExchangeRateSettingsState(
            isOptedIn = SimpleCheckboxState(isOptedIn, ::onOptInClick),
            isOptedOut = SimpleCheckboxState(!isOptedIn, ::onOptOutClick),
            saveButton =
                ButtonState(
                    stringRes(R.string.exchange_rate_opt_in_save),
                    onClick = ::onOptInExchangeRateUsdClick,
                    isEnabled = isOptedIn != isOptedInOriginal,
                    hapticFeedbackType = HapticFeedbackType.Confirm
                ),
            onBack = ::onBack,
            info =
                when {
                    !VersionInfo.IS_CMC_AVAILABLE -> null
                    isTorEnabled == true -> stringRes(R.string.exchange_rate_tor_enabled_footer)
                    else -> stringRes(R.string.exchange_rate_tor_disabled_footer)
                }
        )

    private fun onBack() = navigationRouter.back()

    private fun onOptInClick() = isOptedIn.update { true }

    private fun onOptInExchangeRateUsdClick() = viewModelScope.launch { optInExchangeRate(isOptedIn.value) }

    private fun onOptOutClick() = isOptedIn.update { false }
}
