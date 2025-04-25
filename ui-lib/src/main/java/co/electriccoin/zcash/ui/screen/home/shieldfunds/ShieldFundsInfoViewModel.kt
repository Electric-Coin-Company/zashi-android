package co.electriccoin.zcash.ui.screen.home.shieldfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.ShieldFundsInfoProvider
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CheckboxState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShieldFundsInfoViewModel(
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val shieldFundsInfoProvider: ShieldFundsInfoProvider,
    private val navigationRouter: NavigationRouter,
    private val shieldFunds: ShieldFundsUseCase,
) : ViewModel() {
    val state: StateFlow<ShieldFundsInfoState?> =
        combine(
            getSelectedWalletAccount.observe(),
            shieldFundsInfoProvider.observe(),
        ) { account, infoEnabled ->
            ShieldFundsInfoState(
                onBack = ::onBack,
                primaryButton =
                    ButtonState(
                        onClick = ::onShieldClick,
                        text = stringRes(R.string.home_info_transparent_balance_shield)
                    ),
                secondaryButton =
                    ButtonState(
                        onClick = ::onNotNowClick,
                        text = stringRes(R.string.home_info_transparent_not_now),
                    ),
                transparentAmount = account?.transparent?.balance ?: Zatoshi(0),
                checkbox =
                    CheckboxState(
                        text = stringRes(R.string.home_info_transparent_checkbox),
                        onClick = ::onCheckboxClick,
                        isChecked = !infoEnabled
                    )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onCheckboxClick() = viewModelScope.launch { shieldFundsInfoProvider.flip() }

    private fun onNotNowClick() = navigationRouter.back()

    private fun onBack() = navigationRouter.back()

    private fun onShieldClick() = shieldFunds(closeCurrentScreen = true)
}
