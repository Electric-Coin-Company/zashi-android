package co.electriccoin.zcash.ui.screen.advancedsettings

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.BuildConfig
import co.electriccoin.zcash.ui.common.usecase.GetTransparentAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToInAppBrowserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvancedSettingsViewModel(
    private val getTransparentAddress: GetTransparentAddressUseCase,
    private val navigateToInAppBrowser: NavigateToInAppBrowserUseCase
) : ViewModel() {

    val state = MutableStateFlow(createState()).asStateFlow()

    private fun createState(): AdvancedSettingsState {
        val appId = BuildConfig.COINBASE_APP_ID
        return if (appId.isBlank()) {
            AdvancedSettingsState(
                onBuyWithCoinbase = ::onBuyWithCoinbaseClicked,
                isBuyWithCoinbaseVisible = false
            )
        } else {
            AdvancedSettingsState(
                isBuyWithCoinbaseVisible = true,
                onBuyWithCoinbase = ::onBuyWithCoinbaseClicked,
            )
        }
    }

    private fun onBuyWithCoinbaseClicked(activity: Activity) = viewModelScope.launch {
        val appId = BuildConfig.COINBASE_APP_ID
        val address = getTransparentAddress().address
        val url = "https://pay.coinbase.com/buy/select-asset?appId=${appId}&addresses={\"${address}\":[\"zcash\"]}"
        navigateToInAppBrowser(activity = activity, uri = Uri.parse(url))
    }
}
