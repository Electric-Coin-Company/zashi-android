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
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.fixture.TransactionOverviewFixture
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.compose.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.account.state.TransactionHistorySyncState
import co.electriccoin.zcash.ui.screen.account.state.TransactionOverviewExt
import kotlinx.collections.immutable.persistentListOf

@Preview("Account No History")
@Composable
private fun HistoryLoadingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Account(
                walletSnapshot = WalletSnapshotFixture.new(),
                isKeepScreenOnWhileSyncing = false,
                goBalances = {},
                goSettings = {},
                transactionState = TransactionHistorySyncState.Loading,
                onItemClick = {},
                onTransactionIdClick = {}
            )
        }
    }
}

@Composable
@Preview("Account History List")
private fun HistoryListComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Account(
                walletSnapshot = WalletSnapshotFixture.new(),
                isKeepScreenOnWhileSyncing = false,
                goBalances = {},
                goSettings = {},
                transactionState =
                    TransactionHistorySyncState.Syncing(
                        @Suppress("MagicNumber")
                        persistentListOf(
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(100000000)),
                                null
                            ),
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(200000000)),
                                null
                            ),
                            TransactionOverviewExt(
                                TransactionOverviewFixture.new(netValue = Zatoshi(300000000)),
                                null
                            ),
                        )
                    ),
                onItemClick = {},
                onTransactionIdClick = {}
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun Account(
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    isKeepScreenOnWhileSyncing: Boolean?,
    onItemClick: (TransactionOverviewExt) -> Unit,
    onTransactionIdClick: (String) -> Unit,
    transactionState: TransactionHistorySyncState,
    walletSnapshot: WalletSnapshot,
) {
    Scaffold(topBar = {
        AccountTopAppBar(onSettings = goSettings)
    }) { paddingValues ->
        AccountMainContent(
            walletSnapshot = walletSnapshot,
            isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
            goBalances = goBalances,
            transactionState = transactionState,
            onItemClick = onItemClick,
            onTransactionIdClick = onTransactionIdClick,
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
private fun AccountTopAppBar(onSettings: () -> Unit) {
    SmallTopAppBar(
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
    walletSnapshot: WalletSnapshot,
    isKeepScreenOnWhileSyncing: Boolean?,
    goBalances: () -> Unit,
    onItemClick: (TransactionOverviewExt) -> Unit,
    onTransactionIdClick: (String) -> Unit,
    transactionState: TransactionHistorySyncState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        BalancesStatus(
            walletSnapshot = walletSnapshot,
            goBalances = goBalances,
            modifier =
                Modifier
                    .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingXlarge))

        HistoryContainer(
            transactionState = transactionState,
            onItemClick = onItemClick,
            onTransactionIdClick = onTransactionIdClick,
        )

        if (isKeepScreenOnWhileSyncing == true && walletSnapshot.status == Synchronizer.Status.SYNCING) {
            DisableScreenTimeout()
        }
    }
}

@Composable
private fun BalancesStatus(
    walletSnapshot: WalletSnapshot,
    goBalances: () -> Unit,
    modifier: Modifier = Modifier
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
            walletSnapshot = walletSnapshot,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )
    }
}
