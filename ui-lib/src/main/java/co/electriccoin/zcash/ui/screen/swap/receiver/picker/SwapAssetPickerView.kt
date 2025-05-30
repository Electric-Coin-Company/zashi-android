package co.electriccoin.zcash.ui.screen.swap.receiver.picker

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldScrollPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.home.common.CommonEmptyScreen
import co.electriccoin.zcash.ui.screen.home.common.CommonErrorScreen
import co.electriccoin.zcash.ui.screen.home.common.CommonShimmerLoadingScreen

@Suppress("ForbiddenComment")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapAssetPickerView(state: SwapAssetPickerState?) {
    ZashiScreenModalBottomSheet(
        state = state,
        dragHandle = null,
        includeBottomPadding = false,
        contentWindowInsets = { WindowInsets(0.dp, 0.dp, 0.dp, 0.dp) }
    ) { innerState ->
        BlankBgScaffold(
            topBar = {
                TopAppBar(innerState, windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp))
            }
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .scaffoldScrollPadding(
                            paddingValues = padding,
                            top = padding.calculateTopPadding(),
                            bottom = 0.dp,
                            start = 0.dp,
                            end = 0.dp,
                        )
            ) {
                SearchTextField(innerState)

                when (innerState.data) {
                    is SwapAssetPickerDataState.Error ->
                        CommonErrorScreen(
                            state = innerState.data,
                            modifier = Modifier.fillMaxSize()
                        )

                    SwapAssetPickerDataState.Loading ->
                        CommonShimmerLoadingScreen(
                            shimmerItemsCount = 10,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 20.dp),
                            contentPaddingValues = PaddingValues(24.dp, 12.dp),
                        )

                    is SwapAssetPickerDataState.Success ->
                        if (innerState.data.items.isEmpty()) {
                            CommonEmptyScreen(modifier = Modifier.fillMaxSize())
                        } else {
                            Success(
                                state = innerState.data,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun Success(
    state: SwapAssetPickerDataState.Success,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(state.items) { scrollState.scrollToItem(0) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 20.dp),
        state = scrollState
    ) {
        itemsIndexed(
            state.items,
            key = { _, item -> item.key },
            contentType = { _, item -> item.contentType }
        ) { index, item ->
            Item(item)
            if (index != state.items.lastIndex) {
                ZashiHorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Item(item: ListItemState) {
    ZashiListItem(
        state = item,
        modifier = Modifier.padding(horizontal = 4.dp),
        leading =
            item.icon?.let { icon ->
                {
                    ZashiListItemDefaults.LeadingItem(
                        modifier = Modifier.size(40.dp),
                        icon = icon,
                        badge = item.badge,
                        contentDescription = item.title.getValue()
                    )
                }
            },
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
private fun SearchTextField(innerState: SwapAssetPickerState) {
    ZashiTextField(
        state = innerState.search,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.ic_transaction_search),
                contentDescription = null
            )
        },
        placeholder = {
            Text("Search by name or ticker...")
        },
        singleLine = true,
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    innerState: SwapAssetPickerState,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    ZashiSmallTopAppBar(
        title = "Select chain",
        navigationAction = {
            ZashiTopAppBarCloseNavigation(
                onBack = innerState.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
        windowInsets = windowInsets
    )
}

@PreviewScreens
@Composable
private fun SuccessPreview() =
    ZcashTheme {
        SwapAssetPickerView(
            state =
                SwapAssetPickerState(
                    onBack = {},
                    search = TextFieldState(stringRes("")) {},
                    data =
                        SwapAssetPickerDataState.Success(
                            listOf(
                                ListItemState(
                                    title = stringRes("title"),
                                    subtitle = stringRes("subtitle")
                                ),
                                ListItemState(
                                    title = stringRes("title"),
                                    subtitle = stringRes("subtitle")
                                )
                            )
                        )
                )
        )
    }

@PreviewScreens
@Composable
private fun EmptyPreview() =
    ZcashTheme {
        SwapAssetPickerView(
            state =
                SwapAssetPickerState(
                    onBack = {},
                    search = TextFieldState(stringRes("")) {},
                    data = SwapAssetPickerDataState.Success(listOf())
                )
        )
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        SwapAssetPickerView(
            state =
                SwapAssetPickerState(
                    onBack = {},
                    search = TextFieldState(stringRes("")) {},
                    data = SwapAssetPickerDataState.Loading
                )
        )
    }

@PreviewScreens
@Composable
private fun ErrorPreview() =
    ZcashTheme {
        SwapAssetPickerView(
            state =
                SwapAssetPickerState(
                    onBack = {},
                    search = TextFieldState(stringRes("")) {},
                    data =
                        SwapAssetPickerDataState.Error(
                            stringRes("title"),
                            stringRes("subtitle"),
                            ButtonState(stringRes("text"))
                        )
                )
        )
    }
