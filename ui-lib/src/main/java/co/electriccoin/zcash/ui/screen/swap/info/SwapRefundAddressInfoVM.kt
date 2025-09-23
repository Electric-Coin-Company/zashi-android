package co.electriccoin.zcash.ui.screen.swap.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SwapRefundAddressInfoVM(
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state = getSelectedSwapAsset
        .observe()
        .map {
            SwapRefundAddressInfoState(
                message = if (it != null) {
                    stringRes(
                        R.string.swap_refund_address_info_message,
                        it.tokenTicker,
                        it.chainName
                    )
                } else {
                    stringRes(R.string.swap_refund_address_info_message_empty)
                },
                onBack = ::onBack
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onBack() = navigationRouter.back()
}