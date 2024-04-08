package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.fixture.TransactionsFixture
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState

@Preview("Account No History")
@Composable
private fun HistoryLoadingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Account(
                goBalances = {},
                goSettings = {},
                onTransactionItemAction = {},
                transactionsUiState = TransactionUiState.Loading,
                walletRestoringState = WalletRestoringState.SYNCING,
                balanceState = BalanceStateFixture.new()
            )
        }
    }
}

@Composable
@Preview("Account History List")
private fun HistoryListComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            @Suppress("MagicNumber")
            Account(
                goBalances = {},
                goSettings = {},
                onTransactionItemAction = {},
                transactionsUiState = TransactionUiState.Done(transactions = TransactionsFixture.new()),
                walletRestoringState = WalletRestoringState.NONE,
                balanceState = BalanceState.Available(Zatoshi(123_000_000L), Zatoshi(123_000_000L))
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
internal fun Account(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    onTransactionItemAction: (TrxItemAction) -> Unit,
    transactionsUiState: TransactionUiState,
    walletRestoringState: WalletRestoringState,
) {
    Scaffold(topBar = {
        AccountTopAppBar(
            showRestoring = walletRestoringState == WalletRestoringState.RESTORING,
            onSettings = goSettings
        )
    }) { paddingValues ->
        AccountMainContent(
            balanceState = balanceState,
            goBalances = goBalances,
            transactionState = transactionsUiState,
            walletRestoringState = walletRestoringState,
            onTransactionItemAction = onTransactionItemAction,
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    // We intentionally do not set the bottom and horizontal paddings here. Those are set by the
                    // underlying transaction history composable
                )
        )
    }
}

@Composable
private fun AccountTopAppBar(
    onSettings: () -> Unit,
    showRestoring: Boolean
) {
    SmallTopAppBar(
        restoringLabel =
            if (showRestoring) {
                stringResource(id = R.string.restoring_wallet_label)
            } else {
                null
            },
        showTitleLogo = true,
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
}

@Composable
@Suppress("LongParameterList")
private fun AccountMainContent(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    onTransactionItemAction: (TrxItemAction) -> Unit,
    transactionState: TransactionUiState,
    walletRestoringState: WalletRestoringState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        BalancesStatus(
            balanceState = balanceState,
            goBalances = goBalances,
            modifier =
                Modifier
                    .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        HistoryContainer(
            transactionState = transactionState,
            walletRestoringState = walletRestoringState,
            onTransactionItemAction = onTransactionItemAction,
        )
    }
}

@Composable
private fun BalancesStatus(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.then(
                Modifier
                    .fillMaxWidth()
                    .testTag(AccountTag.BALANCE_VIEWS)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BalanceWidget(
            balanceState = balanceState,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )
    }
}
