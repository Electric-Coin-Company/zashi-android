package co.electriccoin.zcash.ui.screen.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarWithAccountSelection
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
import co.electriccoin.zcash.ui.screen.home.messages.HomeMessage
import co.electriccoin.zcash.ui.screen.home.messages.HomeMessageState
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetState
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.TransactionHistoryWidgetStateFixture
import co.electriccoin.zcash.ui.screen.transactionhistory.widget.createTransactionHistoryWidgets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            NavButtons(paddingValues, state)
            Spacer(Modifier.height(16.dp))
            HomeMessage(state.message)
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
    }
}

@Composable
private fun NavButtons(
    paddingValues: PaddingValues,
    state: HomeState
) {
    Row(
        modifier = Modifier.scaffoldPadding(paddingValues, top = 0.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ZashiBigIconButton(
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(HomeTags.RECEIVE),
            state = state.firstButton,
        )
        ZashiBigIconButton(
            modifier =
                Modifier
                    .weight(1f)
                    .testTag(HomeTags.SEND),
            state = state.secondButton,
        )
        ZashiBigIconButton(
            modifier = Modifier.weight(1f),
            state = state.thirdButton,
        )
        ZashiBigIconButton(
            modifier = Modifier.weight(1f),
            state = state.fourthButton,
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() {
    ZcashTheme {
        var isHomeMessageStateVisible by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()
        HomeView(
            appBarState = ZashiMainTopAppBarStateFixture.new(),
            balanceState = BalanceStateFixture.new(),
            transactionWidgetState = TransactionHistoryWidgetStateFixture.new(),
            state =
                HomeState(
                    firstButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    secondButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    thirdButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    fourthButton =
                        BigIconButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_warning,
                            onClick = {}
                        ),
                    message = null.takeIf { isHomeMessageStateVisible }
                )
        )
    }
}
