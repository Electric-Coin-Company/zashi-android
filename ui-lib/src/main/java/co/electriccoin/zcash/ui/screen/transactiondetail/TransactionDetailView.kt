package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.scaffoldScrollPadding

@Composable
fun TransactionDetailView(
    state: TransactionDetailState,
    appBarState: TopAppBarSubTitleState
) {
    BlankBgScaffold(
        topBar = {
            TransactionDetailTopAppBar(
                onBack = state.onBack,
                appBarState = appBarState,
                state = state,
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .scaffoldScrollPadding(paddingValues),
        ) {
            val rowItems = state.items.filterIsInstance<TransactionDetailItem.Row>()

            state.items.forEachIndexed { _, item ->
                when (item) {
                    is TransactionDetailItem.Header -> Header(item)
                    is TransactionDetailItem.Memo -> Memo(item)
                    is TransactionDetailItem.Note -> Note(item)
                    is TransactionDetailItem.Row -> {
                        val isFirst = rowItems.indexOf(item) == 0

                        if (!isFirst) {
                            ZashiHorizontalDivider(color = ZashiColors.Surfaces.bgPrimary)
                        }

                        Row(
                            item = item,
                            isFirst = isFirst,
                            isLast = rowItems.indexOf(item) == rowItems.lastIndex
                        )
                    }
                    is TransactionDetailItem.ExpandableRow -> TODO()
                }
            }
        }
    }
}

@Suppress("EmptyFunctionBlock", "UnusedParameter")
@Composable
fun Row(
    item: TransactionDetailItem.Row,
    isFirst: Boolean,
    isLast: Boolean
) {
}

@Suppress("EmptyFunctionBlock", "UnusedParameter")
@Composable
fun Note(item: TransactionDetailItem.Note) {
}

@Suppress("EmptyFunctionBlock", "UnusedParameter")
@Composable
fun Memo(item: TransactionDetailItem.Memo) {
}

@Suppress("EmptyFunctionBlock", "UnusedParameter")
@Composable
fun Header(item: TransactionDetailItem.Header) {
}

@Composable
private fun TransactionDetailTopAppBar(
    onBack: () -> Unit,
    appBarState: TopAppBarSubTitleState,
    state: TransactionDetailState
) {
    ZashiSmallTopAppBar(
        subtitle =
            when (appBarState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        showTitleLogo = true,
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
        regularActions = {
            ZashiIconButton(state.bookmarkButton, modifier = Modifier.size(40.dp))
        }
    )
}
