package co.electriccoin.zcash.ui.screen.account.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.compose.StatusDialog
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.account.AccountTag
import co.electriccoin.zcash.ui.screen.balances.model.StatusAction
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeOptIn
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetState
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetStateFixture
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.createTransactionHistoryWidgets
import kotlinx.datetime.Clock

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun HistoryLoadingComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        Account(
            balanceState =
                BalanceStateFixture.new(
                    exchangeRate = ExchangeRateState.OptIn(onDismissClick = {})
                ),
            isHideBalances = false,
            goBalances = {},
            hideStatusDialog = {},
            onContactSupport = {},
            showStatusDialog = null,
            snackbarHostState = SnackbarHostState(),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new(),
            transactionHistoryWidgetState = TransactionHistoryWidgetStateFixture.new(),
            onStatusClick = {},
            walletSnapshot = WalletSnapshotFixture.new(),
            isWalletRestoringState = WalletRestoringState.SYNCING,
        )
    }
}

@Suppress("UnusedPrivateMember")
@Composable
@PreviewScreens
private fun HistoryListComposablePreview() {
    ZcashTheme {
        @Suppress("MagicNumber")
        Account(
            balanceState =
                BalanceState.Available(
                    totalBalance = Zatoshi(value = 123_000_000L),
                    spendableBalance = Zatoshi(value = 123_000_000L),
                    totalShieldedBalance = Zatoshi(value = 123_000_000L),
                    exchangeRate =
                        ExchangeRateState.Data(
                            isLoading = false,
                            isRefreshEnabled = true,
                            currencyConversion =
                                FiatCurrencyConversion(
                                    timestamp = Clock.System.now(),
                                    priceOfZec = 25.0
                                )
                        ) {}
                ),
            isHideBalances = false,
            goBalances = {},
            hideStatusDialog = {},
            onContactSupport = {},
            showStatusDialog = null,
            snackbarHostState = SnackbarHostState(),
            zashiMainTopAppBarState = ZashiMainTopAppBarStateFixture.new(),
            transactionHistoryWidgetState = TransactionHistoryWidgetStateFixture.new(),
            onStatusClick = {},
            walletSnapshot = WalletSnapshotFixture.new(),
            isWalletRestoringState = WalletRestoringState.SYNCING,
        )
    }
}

@Composable
@Suppress("LongParameterList")
internal fun Account(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    isHideBalances: Boolean,
    hideStatusDialog: () -> Unit,
    onContactSupport: (StatusAction.Error) -> Unit,
    showStatusDialog: StatusAction.Detailed?,
    snackbarHostState: SnackbarHostState,
    zashiMainTopAppBarState: ZashiMainTopAppBarState?,
    transactionHistoryWidgetState: TransactionHistoryWidgetState,
    isWalletRestoringState: WalletRestoringState,
    onStatusClick: (StatusAction) -> Unit,
    walletSnapshot: WalletSnapshot,
) {
    BlankBgScaffold(
        topBar = {
            ZashiMainTopAppBar(zashiMainTopAppBarState)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        AccountMainContent(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            goBalances = goBalances,
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
                    // We intentionally do not set the bottom and horizontal paddings here. Those are set by the
                    // underlying transaction history composable
                ),
            paddingValues = paddingValues,
            transactionHistoryWidgetState = transactionHistoryWidgetState,
            isWalletRestoringState = isWalletRestoringState,
            onStatusClick = onStatusClick,
            walletSnapshot = walletSnapshot,
        )

        // Show synchronization status popup
        if (showStatusDialog != null) {
            StatusDialog(
                statusAction = showStatusDialog,
                onConfirm = hideStatusDialog,
                onReport = { status ->
                    hideStatusDialog()
                    onContactSupport(status)
                }
            )
        }
    }
}

@Composable
@Suppress("LongParameterList", "ModifierNotUsedAtRoot", "LongMethod")
private fun AccountMainContent(
    balanceState: BalanceState,
    goBalances: () -> Unit,
    isHideBalances: Boolean,
    transactionHistoryWidgetState: TransactionHistoryWidgetState,
    isWalletRestoringState: WalletRestoringState,
    onStatusClick: (StatusAction) -> Unit,
    walletSnapshot: WalletSnapshot,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
    Box {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

            val bottomPadding =
                animateDpAsState(
                    targetValue =
                        if (balanceState.exchangeRate is ExchangeRateState.OptIn) {
                            112.dp
                        } else {
                            0.dp
                        },
                    animationSpec =
                        if (balanceState.exchangeRate is ExchangeRateState.OptIn) {
                            snap()
                        } else {
                            spring(visibilityThreshold = .1.dp)
                        },
                    label = "bottom padding animation"
                )

            BalancesStatus(
                balanceState = balanceState,
                goBalances = goBalances,
                isHideBalances = isHideBalances,
                modifier =
                    Modifier
                        .padding(
                            start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                            end = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                            bottom = bottomPadding.value
                        ),
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {
                createRestoringProgressView(
                    onStatusClick = onStatusClick,
                    walletRestoringState = isWalletRestoringState,
                    walletSnapshot = walletSnapshot,
                )

                createTransactionHistoryWidgets(
                    state = transactionHistoryWidgetState
                )
            }
        }

        AnimatedVisibility(
            visible = balanceState.exchangeRate is ExchangeRateState.OptIn,
            enter = EnterTransition.None,
            exit = fadeOut() + slideOutVertically(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(100.dp + paddingValues.calculateTopPadding()))
                StyledExchangeOptIn(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    state =
                        (balanceState.exchangeRate as? ExchangeRateState.OptIn) ?: ExchangeRateState.OptIn(
                            onDismissClick = {},
                        )
                )
            }
        }
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
            onReferenceClick = goBalances,
        )
    }
}
