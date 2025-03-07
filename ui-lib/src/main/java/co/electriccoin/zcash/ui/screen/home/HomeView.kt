package co.electriccoin.zcash.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarWithAccountSelection
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiBigIconButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.balances.BalanceState
import co.electriccoin.zcash.ui.screen.balances.BalanceWidget
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeOptIn
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetState
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetStateFixture
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.createTransactionHistoryWidgets

@Composable
internal fun HomeView(
    appBarState: ZashiMainTopAppBarState?,
    balanceState: BalanceState,
    transactionWidgetState: TransactionHistoryWidgetState,
    state: HomeState
) {
    BlankBgScaffold(
        topBar = { ZashiTopAppBarWithAccountSelection(appBarState) }
    ) { paddingValues ->
        Content(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + 24.dp),
            paddingValues = paddingValues,
            transactionHistoryWidgetState = transactionWidgetState,
            balanceState = balanceState,
            state = state
        )
    }
}

@Composable
private fun Content(
    transactionHistoryWidgetState: TransactionHistoryWidgetState,
    paddingValues: PaddingValues,
    balanceState: BalanceState,
    state: HomeState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

            BalanceWidget(
                modifier =
                    Modifier
                        .padding(
                            start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                            end = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        ),
                balanceState = balanceState,
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

            Row(
                modifier = Modifier.scaffoldPadding(paddingValues, top = 0.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZashiBigIconButton(
                    modifier = Modifier.weight(1f),
                    state = state.receiveButton,
                )
                ZashiBigIconButton(
                    modifier = Modifier.weight(1f),
                    state = state.sendButton,
                )
                ZashiBigIconButton(
                    modifier = Modifier.weight(1f),
                    state = state.scanButton,
                )
                ZashiBigIconButton(
                    modifier = Modifier.weight(1f),
                    state = state.moreButton,
                )
            }

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
            ) {
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
                Spacer(modifier = Modifier.height(66.dp + paddingValues.calculateTopPadding()))
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

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        HomeView(
            appBarState = ZashiMainTopAppBarStateFixture.new(),
            balanceState = BalanceStateFixture.new(),
            transactionWidgetState = TransactionHistoryWidgetStateFixture.new(),
            state =
                HomeState(
                    receiveButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    sendButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    scanButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    moreButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                )
        )
    }
