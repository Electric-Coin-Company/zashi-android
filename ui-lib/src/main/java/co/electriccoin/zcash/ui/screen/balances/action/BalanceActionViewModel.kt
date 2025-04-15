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
            title = stringRes("Spendable Balance"),
            message = createMessage(account),
            positive = createPositiveButton(account),
            onBack = ::onBack,
            rows = createInfoRows(account),
            shieldButton = createShieldButtonState(account)
        )
    }

    private fun createMessage(account: WalletAccount): StringResource {
        val pending = when {
            account.totalBalance == account.spendableBalance && !account.isPending ->
                stringRes("All your funds are shielded and spendable.")

            account.isPending || account.isProcessingZeroAvailableBalance() ->
                stringRes("Pending transactions are getting mined and confirmed.")
            else -> null
        }

        val shielding =
            stringRes("Shield your transparent ZEC to make it spendable and private. Shielding transparent funds will create a shielding in-wallet transaction, consolidating your transparent and shielded funds. (Typical fee: .001 ZEC)")
                .takeIf { account.transparent.isShieldingAvailable }

        return if (pending != null && shielding != null) {
            pending + stringRes("\n\n") + shielding
        } else {
            listOfNotNull(pending, shielding).reduceOrNull { acc, stringResource -> acc + stringResource } ?: stringRes(
                ""
            )
        }
    }

    private fun createPositiveButton(account: WalletAccount) = ButtonState(
        text = if (account.transparent.isShieldingAvailable) stringRes("Dismiss") else stringRes("Ok"),
        onClick = ::onBack
    )

    private fun createInfoRows(account: WalletAccount) = listOfNotNull(
        BalanceActionRowState(
            title = stringRes("Shielded ZEC (Spendable)"),
            icon = imageRes(R.drawable.ic_balance_shield),
            value = stringRes(R.string.general_zec, stringRes(account.spendableBalance))
        ),
        if (!account.isProcessingZeroAvailableBalance()) {
            BalanceActionRowState(
                title = stringRes("Pending"),
                icon = loadingImageRes(),
                value = stringRes(R.string.general_zec, stringRes(account.totalBalance))
            )
        } else {
            BalanceActionRowState(
                title = stringRes("Pending"),
                icon = loadingImageRes(),
                value = stringRes(R.string.general_zec, stringRes(account.pendingBalance))
            ).takeIf { account.isPending }
        },
    )

    private fun createShieldButtonState(account: WalletAccount): BalanceShieldButtonState? {
        return BalanceShieldButtonState(
            amount = account.transparent.balance,
            onShieldClick = ::onShieldClick
        ).takeIf { account.transparent.isShieldingAvailable }
    }

    private fun onBack() = navigationRouter.back()

    private fun onShieldClick() = shieldFunds(closeCurrentScreen = true)
}