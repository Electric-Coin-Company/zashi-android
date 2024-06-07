package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerTabs(
    modifier: Modifier,
    pagerState: PagerState,
    tabs: List<String>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onTabSelected: (index: Int) -> Unit = {},
) {
    TabRow(
        modifier = modifier
            .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig)
            .border(ZcashTheme.dimens.spacingTiny, ZcashTheme.colors.layoutStroke),
        selectedTabIndex = pagerState.currentPage,
        divider = {},
        indicator = {},
    ) {
        tabs.forEachIndexed { index, tab ->
            PagerTab(
                title = tab,
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        onTabSelected(index)
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@Composable
private fun PagerTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Tab(
        modifier = Modifier
            .fillMaxWidth(),
        selected = selected,
        onClick = onClick,
        selectedContentColor = Color.Transparent,
        unselectedContentColor = ZcashTheme.colors.layoutStroke
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (selected) Color.Transparent else ZcashTheme.colors.layoutStroke
                )
                .padding(vertical = ZcashTheme.dimens.spacingMid, horizontal = ZcashTheme.dimens.spacingXtiny),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ZcashTheme.dimens.spacingXtiny),
                text = title,
                color = if (selected) ZcashTheme.colors.textCommon else MaterialTheme.colorScheme.onPrimary,
                style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
