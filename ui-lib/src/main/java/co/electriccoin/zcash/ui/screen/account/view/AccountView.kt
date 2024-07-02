package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.compose.StatusDialog
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarHideBalancesNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.fixture.TransactionsFixture
import co.electriccoin.zcash.ui.screen.account.model.TransactionUiState
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction

@Preview("Account No History")
@Composable
private fun HistoryLoadingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        Account(
            balanceState = BalanceStateFixture.new(),
            isHideBalances = false,
            goBalances = {},
            goSettings = {},
            hideStatusDialog = {},
            onHideBalances = {},
            onStatusClick = {},
            onTransactionItemAction = {},
            showStatusDialog = null,
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            transactionsUiState = TransactionUiState.Loading,
            walletRestoringState = WalletRestoringState.SYNCING,
            walletSnapshot = WalletSnapshotFixture.new(),
        )
    }
}

@Composable
@Preview("Account History List")
private fun HistoryListComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        @Suppress("MagicNumber")
        Account(
            balanceState =
                BalanceState.Available(
                    Zatoshi(123_000_000L),
                    Zatoshi(123_000_000L)
                ),
            isHideBalances = false,
            goBalances = {},
            goSettings = {},
            hideStatusDialog = {},
            onHideBalances = {},
            onStatusClick = {},
            onTransactionItemAction = {},
            showStatusDialog = null,
            snackbarHostState = SnackbarHostState(),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            transactionsUiState = TransactionUiState.Done(transactions = TransactionsFixture.new()),
            walletRestoringState = WalletRestoringState.NONE,
            walletSnapshot = WalletSnapshotFixture.new(),
        )
    }
}

@Composable
@Suppress("LongParameterList")
internal fun Account(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    isHideBalances: Boolean,
    hideStatusDialog: () -> Unit,
    onHideBalances: (Boolean) -> Unit,
    onStatusClick: (StatusAction) -> Unit,
    onTransactionItemAction: (TrxItemAction) -> Unit,
    showStatusDialog: StatusAction.Detailed?,
    snackbarHostState: SnackbarHostState,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    transactionsUiState: TransactionUiState,
    walletRestoringState: WalletRestoringState,
    walletSnapshot: WalletSnapshot,
) {
    BlankBgScaffold(
        topBar = {
            AccountTopAppBar(
                isHideBalances = isHideBalances,
                onHideBalances = onHideBalances,
                onSettings = goSettings,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        AccountMainContent(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            goBalances = goBalances,
            onStatusClick = onStatusClick,
            onTransactionItemAction = onTransactionItemAction,
            transactionState = transactionsUiState,
            isWalletRestoringState = walletRestoringState,
            walletSnapshot = walletSnapshot,
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    // We intentionally do not set the bottom and horizontal paddings here. Those are set by the
                    // underlying transaction history composable
                )
        )

        // Show synchronization status popup
        if (showStatusDialog != null) {
            StatusDialog(
                statusAction = showStatusDialog,
                onDone = hideStatusDialog
            )
        }
    }
}

@Composable
private fun AccountTopAppBar(
    isHideBalances: Boolean,
    onHideBalances: (Boolean) -> Unit,
    onSettings: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
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
        },
        navigationAction = {
            TopAppBarHideBalancesNavigation(
                contentDescription = stringResource(id = R.string.hide_balances_content_description),
                iconVector =
                    ImageVector.vectorResource(
                        if (isHideBalances) {
                            R.drawable.ic_hide_balances_on
                        } else {
                            R.drawable.ic_hide_balances_off
                        }
                    ),
                onClick = { onHideBalances(!isHideBalances) },
                modifier = Modifier.testTag(CommonTag.HIDE_BALANCES_TOP_BAR_BUTTON)
            )
        },
    )
}

@Composable
@Suppress("LongParameterList")
private fun AccountMainContent(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    isHideBalances: Boolean,
    isWalletRestoringState: WalletRestoringState,
    onTransactionItemAction: (TrxItemAction) -> Unit,
    onStatusClick: (StatusAction) -> Unit,
    transactionState: TransactionUiState,
    walletSnapshot: WalletSnapshot,
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
            isHideBalances = isHideBalances,
            modifier =
                Modifier
                    .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        HistoryContainer(
            isHideBalances = isHideBalances,
            onStatusClick = onStatusClick,
            onTransactionItemAction = onTransactionItemAction,
            transactionState = transactionState,
            walletRestoringState = isWalletRestoringState,
            walletSnapshot = walletSnapshot,
        )
    }
}

@Composable
private fun BalancesStatus(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    isHideBalances: Boolean,
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
            isHideBalances = isHideBalances,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )
    }
}
