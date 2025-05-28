package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiInScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.asScaffoldScrollPaddingValues
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapPickerView(state: SwapPickerState?) {
    ZashiInScreenModalBottomSheet(
        state = state,
        dragHandle = null
    ) { innerState ->
        BlankBgScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(innerState) }
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            padding.asScaffoldScrollPaddingValues(
                                top = padding.calculateTopPadding() + 8.dp,
                            ),
                        )
            ) {
                SearchTextField(innerState)

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentPadding = PaddingValues(top = 20.dp)
                ) {
                    itemsIndexed(innerState.items) { index, item ->
                        Item(item)
                        if (index != innerState.items.lastIndex) {
                            ZashiHorizontalDivider(
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(item: ListItemState) {
    ZashiListItem(
        state = item,
        modifier = Modifier.padding(horizontal = 4.dp),
        leading = {
            ZashiListItemDefaults.LeadingItem(
                modifier = Modifier.size(40.dp),
                icon = item.icon,
                contentDescription = item.title.getValue()
            )
        },
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
private fun SearchTextField(innerState: SwapPickerState) {
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
        }
    )
}

@Composable
private fun TopAppBar(innerState: SwapPickerState) {
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
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        SwapPickerView(
            state =
                SwapPickerState(
                    onBack = {},
                    search = TextFieldState(stringRes("")) {},
                    items =
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
    }
