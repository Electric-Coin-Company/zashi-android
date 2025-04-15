package co.electriccoin.zcash.ui.screen.balances.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.usecase.ShieldFundsUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

class BalanceActionViewModel(
    accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter,
    private val shieldFunds: ShieldFundsUseCase,
) : ViewModel() {
    val state = accountDataSource.selectedAccount
        .mapNotNull {
            createState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(accountDataSource.allAccounts.value.orEmpty().firstOrNull { it.isSelected })
        )

    private fun createState(account: WalletAccount?): BalanceActionState? {
        if (account == null) return null

        return BalanceActionState(
            title = stringRes(R.string.balance_action_title),
            message = createMessage(account),
            positive = createPositiveButton(account),
            onBack = ::onBack,
            rows = createInfoRows(account),
            shieldButton = createShieldButtonState(account)
        )
    }

    private fun createMessage(account: WalletAccount): StringResource {
        val pending = when {
            account.totalShieldedBalance == account.spendableShieldedBalance ->
                stringRes(R.string.balance_action_all_shielded)

            account.totalShieldedBalance > account.spendableShieldedBalance ->
                stringRes(R.string.balance_action_pending)

            else -> null
        }

        val shielding = stringRes(R.string.balance_action_shield_message).takeIf { account.isShieldingAvailable }

        return if (pending != null && shielding != null) {
            pending + stringRes("\n\n") + shielding
        } else {
            pending ?: shielding ?: stringRes("")
        }
    }

    private fun createPositiveButton(account: WalletAccount) = ButtonState(
        text = if (account.isShieldingAvailable) {
            stringRes(R.string.general_dismiss)
        } else {
            stringRes(R.string.general_ok)
        },
        onClick = ::onBack
    )

    private fun createInfoRows(account: WalletAccount) = listOfNotNull(
        BalanceActionRowState(
            title = stringRes(R.string.balance_action_info_shielded),
            icon = imageRes(R.drawable.ic_balance_shield),
            value = stringRes(R.string.general_zec, stringRes(account.spendableShieldedBalance))
        ),
        when {
            account.totalShieldedBalance > account.spendableShieldedBalance && account.isShieldedPending ->
                BalanceActionRowState(
                    title = stringRes(R.string.balance_action_info_pending),
                    icon = loadingImageRes(),
                    value = stringRes(R.string.general_zec, stringRes(account.pendingShieldedBalance))
                )

            account.totalShieldedBalance > account.spendableShieldedBalance ->
                BalanceActionRowState(
                    title = stringRes(R.string.balance_action_info_pending),
                    icon = loadingImageRes(),
                    value = stringRes(
                        R.string.general_zec,
                        stringRes(account.totalShieldedBalance - account.spendableShieldedBalance)
                    )
                )

            else -> null
        },
    )

    private fun createShieldButtonState(account: WalletAccount): BalanceShieldButtonState? {
        return BalanceShieldButtonState(
            amount = account.transparent.balance,
            onShieldClick = ::onShieldClick
        ).takeIf { account.isShieldingAvailable }
    }

    private fun onBack() = navigationRouter.back()

    private fun onShieldClick() = shieldFunds(closeCurrentScreen = true)
}