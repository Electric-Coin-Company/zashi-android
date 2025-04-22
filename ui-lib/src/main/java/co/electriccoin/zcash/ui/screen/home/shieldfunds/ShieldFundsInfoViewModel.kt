package co.electriccoin.zcash.ui.screen.home.shieldfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.ShieldFundsData
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.RemindShieldFundsLaterUseCase
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class ShieldFundsInfoViewModel(
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    shieldFundsRepository: ShieldFundsRepository,
    private val navigationRouter: NavigationRouter,
    private val remindShieldFundsLater: RemindShieldFundsLaterUseCase,
    private val shieldFunds: ShieldFundsUseCase,
) : ViewModel() {
    private val lockoutDuration =
        shieldFundsRepository
            .availability
            .filterIsInstance<ShieldFundsData.Available>()
            .take(1)
            .map { it.lockoutDuration }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    val state: StateFlow<ShieldFundsInfoState?> =
        combine(
            getSelectedWalletAccount.observe(),
            lockoutDuration.filterNotNull(),
        ) { account, lockoutDuration ->
            ShieldFundsInfoState(
                onBack = ::onBack,
                primaryButton =
                    ButtonState(
                        onClick = ::onShieldClick,
                        text = stringRes(R.string.home_info_transparent_balance_shield)
                    ),
                secondaryButton =
                    ButtonState(
                        onClick = ::onRemindMeClick,
                        text = stringRes(R.string.general_remind_me_in, stringRes(lockoutDuration.res))
                    ),
                transparentAmount = account?.transparent?.balance ?: Zatoshi(0)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onRemindMeClick() = viewModelScope.launch { remindShieldFundsLater() }

    private fun onBack() = navigationRouter.back()

    private fun onShieldClick() = shieldFunds(closeCurrentScreen = true)
}
