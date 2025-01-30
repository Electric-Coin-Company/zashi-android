@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    mainAppBarState: ZashiMainTopAppBarState?,
    listState: LazyListState = rememberLazyListState()
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = ZashiDimensions.Spacing.spacing3xl,
                            end = ZashiDimensions.Spacing.spacing3xl,
                            top = 8.dp
                        ),
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
                            text = stringRes(stringResource(R.string.transaction_history_search)).getValue(),
                            style = ZashiTypography.textMd,
                            color = ZashiColors.Inputs.Default.text,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                BadgeIconButton(
                    state = state.filterButton
                )
            }

            when (state) {
                is TransactionHistoryState.Data ->
                    Data(
                        paddingValues = paddingValues,
                        listState = listState,
                        state = state,
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                    )

                is TransactionHistoryState.Empty -> Empty(modifier = Modifier.fillMaxSize())
                is TransactionHistoryState.Loading ->
                    Loading(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(top = 20.dp)
                    )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Data(
    paddingValues: PaddingValues,
    listState: LazyListState,
    state: TransactionHistoryState.Data,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues.asScaffoldScrollPaddingValues(top = 26.dp),
        state = listState
    ) {
        state.items.forEachIndexed { index, item ->
            when (item) {
                is TransactionHistoryItem.Header ->
                    stickyHeader(
                        contentType = item.contentType,
                        key = item.key
                    ) {
                        HeaderItem(
                            item,
                            modifier =
                                Modifier
                                    .fillParentMaxWidth()
                                    .background(ZashiColors.Surfaces.bgPrimary)
                                    .animateItem()
                        )
                    }

                is TransactionHistoryItem.Transaction ->
                    item(
                        contentType = item.contentType,
                        key = item.key,
                    ) {
                        TransactionItem(
                            item = item,
                            index = index,
                            state = state,
                            modifier = Modifier.animateItem()
                        )
                    }
            }
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    TransactionShimmerLoading(
        modifier = modifier,
        shimmerItemsCount = 10,
    )
}

@Composable
private fun Empty(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        TransactionShimmerLoading(
            modifier = Modifier.padding(top = 32.dp),
            shimmerItemsCount = 3,
            disableShimmer = true,
            showDivider = false,
            contentPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                EMPTY_GRADIENT_THRESHOLD to ZashiColors.Surfaces.bgPrimary,
                                1f to ZashiColors.Surfaces.bgPrimary,
                            )
                    ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(128.dp))
            Image(
                painter = painterResource(R.drawable.ic_transaction_widget_empty),
                contentDescription = null,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "No results",
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textLg,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "We tried but couldn’t find anything.",
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm,
            )
        }
    }
}

@Composable
private fun HeaderItem(
    item: TransactionHistoryItem.Header,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = item.title.getValue(),
            style = ZashiTypography.textMd,
            color = ZashiColors.Text.textTertiary,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TransactionItem(
    item: TransactionHistoryItem.Transaction,
    index: Int,
    state: TransactionHistoryState.Data,
    modifier: Modifier = Modifier
) {
    val previousItem = if (index != 0) state.items[index - 1] else null
    val nextItem = if (index != state.items.lastIndex) state.items[index + 1] else null

    Column(
        modifier = modifier,
    ) {
        if (previousItem is TransactionHistoryItem.Header) {
            Spacer(Modifier.height(6.dp))
        }

        Transaction(
            modifier = Modifier.padding(horizontal = 4.dp),
            state = item.state,
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
        )

        if (index != state.items.lastIndex && nextItem is TransactionHistoryItem.Transaction) {
            ZashiHorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        } else if (index != state.items.lastIndex && nextItem !is TransactionHistoryItem.Transaction) {
            Spacer(
                modifier = Modifier.height(26.dp)
            )
        }
    }
}

@Composable
private fun BadgeIconButton(
    state: IconButtonState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clickable(
                    // Remove the ripple effect rather than clipping the badge icon
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state.onClick,
                    role = Role.Button,
                )
    ) {
        Image(
            painter = painterResource(state.icon),
            contentDescription = state.contentDescription?.getValue(),
        )

        state.badge?.let {
            @Suppress("MagicNumber")
            Text(
                modifier =
                    Modifier
                        .offset(8.dp, (-8).dp)
                        .size(21.dp)
                        .border(2.dp, ZashiColors.Surfaces.bgPrimary, CircleShape)
                        .padding(2.dp)
                        .background(ZashiColors.Utility.Gray.utilityGray900, CircleShape)
                        .align(Alignment.TopEnd)
                        .padding(top = 1.dp),
                text = it.getValue(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Surfaces.bgPrimary,
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium
            )
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
        title = stringResource(R.string.transaction_history_screen_title),
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

private const val EMPTY_GRADIENT_THRESHOLD = .28f

@PreviewScreens
@Composable
private fun DataPreview() =
    ZcashTheme {
        TransactionHistoryView(
            state =
                TransactionHistoryState.Data(
                    onBack = {},
                    search = TextFieldState(stringRes(value = "")) {},
                    filterButton =
                        IconButtonState(
                            icon = R.drawable.ic_transaction_filters,
                            badge = stringRes("1"),
                            onClick = {}
                        ),
                    items =
                        listOf(
                            TransactionHistoryItem.Header(
                                title = stringRes("Header")
                            ),
                            TransactionHistoryItem.Transaction(
                                state = TransactionStateFixture.new(),
                            ),
                            TransactionHistoryItem.Transaction(
                                state = TransactionStateFixture.new(),
                            ),
                            TransactionHistoryItem.Header(
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

@PreviewScreens
@Composable
private fun EmptyPreview() =
    ZcashTheme {
        TransactionHistoryView(
            state =
                TransactionHistoryState.Empty(
                    onBack = {},
                    search = TextFieldState(stringRes(value = "")) {},
                    filterButton =
                        IconButtonState(
                            icon = R.drawable.ic_transaction_filters,
                            badge = stringRes("1"),
                            onClick = {}
                        )
                ),
            appBarState = TopAppBarSubTitleState.None,
            mainAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        TransactionHistoryView(
            state =
                TransactionHistoryState.Loading(
                    onBack = {},
                    search = TextFieldState(stringRes(value = "")) {},
                    filterButton =
                        IconButtonState(
                            icon = R.drawable.ic_transaction_filters,
                            badge = stringRes("1"),
                            onClick = {}
                        )
                ),
            appBarState = TopAppBarSubTitleState.None,
            mainAppBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }
