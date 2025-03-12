@file:OptIn(ExperimentalFoundationApi::class)

package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview
@Composable
private fun PagerTabsPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            PagerTabs(
                pagerState = rememberPagerState { 2 },
                tabs = persistentListOf("First", "Second"),
            )
        }
    }
}

@Preview
@Composable
private fun PagerTabsDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        BlankSurface {
            PagerTabs(
                pagerState = rememberPagerState { 2 },
                tabs = persistentListOf("First", "Second"),
            )
        }
    }
}

@Composable
fun PagerTabs(
    pagerState: PagerState,
    tabs: ImmutableList<String>,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onTabSelect: (index: Int) -> Unit = {},
) {
    TabRow(
        modifier = modifier.border(ZcashTheme.dimens.spacingTiny, ZcashTheme.colors.layoutStroke),
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
                        onTabSelect(index)
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
        modifier =
            Modifier
                .fillMaxWidth(),
        selected = selected,
        onClick = onClick,
        selectedContentColor = Color.Transparent,
        unselectedContentColor = ZcashTheme.colors.layoutStroke
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        if (selected) Color.Transparent else ZcashTheme.colors.layoutStroke
                    ).padding(vertical = ZcashTheme.dimens.spacingMid, horizontal = ZcashTheme.dimens.spacingXtiny),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ZcashTheme.dimens.spacingXtiny),
                text = title,
                color = if (selected) ZcashTheme.colors.textPrimary else ZcashTheme.colors.textSecondary,
                style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
