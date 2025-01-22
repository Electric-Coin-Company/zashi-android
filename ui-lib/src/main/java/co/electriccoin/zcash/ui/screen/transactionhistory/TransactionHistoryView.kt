package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.asScaffoldScrollPaddingValues
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture

@Composable
fun TransactionHistoryView(
    state: TransactionHistoryState,
    appBarState: TopAppBarSubTitleState,
    mainAppBarState: ZashiMainTopAppBarState?
) {
    BlankBgScaffold(
        topBar = {
            TransactionHistoryAppBar(
                appBarState = appBarState,
                mainAppBarState = mainAppBarState,
                state = state,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ZashiDimensions.Spacing.spacing3xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZashiTextField(
                    modifier = Modifier.weight(1f),
                    state = state.search,
                    singleLine = true,
                    maxLines = 1,
                    prefix = {
                        Image(
                            painter = painterResource(R.drawable.ic_transaction_search),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(ZashiColors.Dropdowns.Default.text)
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringRes("Search").getValue(),
                            style = ZashiTypography.textMd,
                            color = ZashiColors.Inputs.Default.text,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                ZashiIconButton(
                    state = state.filterButton
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = paddingValues.asScaffoldScrollPaddingValues(top = 0.dp)
            ) {
                itemsIndexed(
                    items = state.items,
                    key = { _, item -> item.key },
                    contentType = { _, item -> item.contentType },
                ) { index, item ->
                    when (item) {
                        is TransactionHistoryItem.Header -> {
                            Column(
                                modifier = Modifier.animateItem()
                            ) {
                                Spacer(Modifier.height(32.dp))
                                Text(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    text = item.title.getValue(),
                                    style = ZashiTypography.textMd,
                                    color = ZashiColors.Text.textTertiary,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                        }

                        is TransactionHistoryItem.Transaction -> {
                            Column(
                                modifier = Modifier.animateItem()
                            ) {
                                Transaction(
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    state = item.state,
                                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
                                )

                                if (index != state.items.lastIndex && state.items[index + 1] is TransactionHistoryItem
                                    .Transaction
                                ) {
                                    ZashiHorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryAppBar(
    appBarState: TopAppBarSubTitleState,
    mainAppBarState: ZashiMainTopAppBarState?,
    state: TransactionHistoryState
) {
    ZashiSmallTopAppBar(
        title = stringRes("Transactions").getValue(),
        subtitle =
        when (appBarState) {
            TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
            TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
            TopAppBarSubTitleState.None -> null
        },
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = state.onBack)
        },
        hamburgerMenuActions = {
            mainAppBarState?.balanceVisibilityButton?.let {
                ZashiIconButton(it, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(20.dp))
            }
        }
    )
}

@PreviewScreens
@Composable
private fun Preview() = ZcashTheme {
    TransactionHistoryView(
        state = TransactionHistoryState(
            onBack = {},
            search = TextFieldState(stringRes(value = "")) {},
            filterButton = IconButtonState(
                icon = R.drawable.ic_transaction_filters,
                badge = stringRes("1"),
                onClick = {}
            ),
            items = listOf(
                TransactionHistoryItem.Header(
                    key = "week 0",
                    title = stringRes("Header")
                ),
                TransactionHistoryItem.Transaction(
                    state = TransactionStateFixture.new(),
                ),
                TransactionHistoryItem.Transaction(
                    state = TransactionStateFixture.new(),
                ),
                TransactionHistoryItem.Header(
                    key = "week 1",
                    title = stringRes("Header 2")
                ),
                TransactionHistoryItem.Transaction(
                    state = TransactionStateFixture.new()
                ),
                TransactionHistoryItem.Transaction(
                    state = TransactionStateFixture.new()
                ),
            )
        ),
        appBarState = TopAppBarSubTitleState.None,
        mainAppBarState = ZashiMainTopAppBarStateFixture.new()
    )
}