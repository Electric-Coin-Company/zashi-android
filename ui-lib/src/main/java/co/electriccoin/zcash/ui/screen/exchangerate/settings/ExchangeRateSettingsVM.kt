package co.electriccoin.zcash.ui.screen.exchangerate.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.ExchangeRateOptInStorageProvider
import co.electriccoin.zcash.ui.common.provider.PersistableWalletTorProvider
import co.electriccoin.zcash.ui.common.provider.TorState
import co.electriccoin.zcash.ui.common.usecase.OptInExchangeRateUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ExchangeRateSettingsVM(
    private val navigationRouter: NavigationRouter,
    private val optInExchangeRate: OptInExchangeRateUseCase,
    private val persistableWalletTorProvider: PersistableWalletTorProvider,
    private val exchangeRateOptInStorageProvider: ExchangeRateOptInStorageProvider
) : ViewModel() {
    private var isOptedInOriginal = false

    private val isOptedIn = MutableStateFlow(false)

    val state =
        isOptedIn
            .map {
                createState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(isOptedIn.value)
            )

    init {
        viewModelScope.launch {
            isOptedInOriginal = exchangeRateOptInStorageProvider.get() == true
            isOptedIn.update { isOptedInOriginal }
        }
    }

    private fun createState(isOptedIn: Boolean) =
        ExchangeRateSettingsState(
            isOptedIn = SimpleCheckboxState(isOptedIn, ::onOptInClick),
            isOptedOut = SimpleCheckboxState(!isOptedIn, ::onOptOutClick),
            saveButton =
                ButtonState(
                    stringRes(R.string.exchange_rate_opt_in_save),
                    onClick = ::onOptInExchangeRateUsdClick,
                    isEnabled = isOptedIn != isOptedInOriginal
                ),
            onBack = ::onBack
        )

    private fun onBack() = navigationRouter.back()

    private fun onOptInClick() =
        viewModelScope.launch {
            val torState = persistableWalletTorProvider.get()
            if (torState in listOf(TorState.EXPLICITLY_DISABLED, TorState.IMPLICITLY_DISABLED)) {
                navigationRouter.forward(ExchangeRateTorSettingsArgs)
            } else {
                isOptedIn.update { true }
            }
        }

    private fun onOptInExchangeRateUsdClick() = viewModelScope.launch { optInExchangeRate(isOptedIn.value) }

    private fun onOptOutClick() = isOptedIn.update { false }
}
