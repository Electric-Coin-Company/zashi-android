package co.electriccoin.zcash.ui.screen.home.transparentbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TransparentBalanceInfoViewModel(
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<TransparentBalanceInfoState?> =
        getSelectedWalletAccount
            .observe()
            .map {
                TransparentBalanceInfoState(
                    onBack = { navigationRouter.back() },
                    primaryButton =
                        ButtonState(
                            onClick = { navigationRouter.back() },
                            text = stringRes(R.string.home_info_transparent_balance_shield)
                        ),
                    secondaryButton =
                        ButtonState(
                            onClick = { navigationRouter.back() },
                            text = stringRes(R.string.general_remind_me_later)
                        ),
                    transparentAmount = it?.transparent?.balance ?: Zatoshi(0)
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )
}
