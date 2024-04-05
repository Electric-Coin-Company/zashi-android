package co.electriccoin.zcash.ui.screen.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.compose.DisableScreenTimeout
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.NavigationTabText
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.home.ForcePage
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Preview("Home")
@Composable
private fun ComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Home(
                isKeepScreenOnWhileSyncing = false,
                forcePage = null,
                onPageChange = {},
                subScreens = persistentListOf(),
                walletSnapshot = WalletSnapshotFixture.new(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun Home(
    isKeepScreenOnWhileSyncing: Boolean?,
    forcePage: ForcePage?,
    onPageChange: (HomeScreenIndex) -> Unit,
    subScreens: ImmutableList<TabItem>,
    walletSnapshot: WalletSnapshot?,
) {
    val pagerState =
        rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { subScreens.size }
        )

    // Listening for the current page change
    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPage
        }.distinctUntilChanged()
            .collect { page ->
                Twig.info { "Current pager page: $page" }
                onPageChange(HomeScreenIndex.fromIndex(page))
            }
    }

    // Force page change e.g. when system back navigation event detected
    forcePage?.let {
        LaunchedEffect(forcePage) {
            pagerState.animateScrollToPage(forcePage.currentPage.ordinal)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout {
        val (pager, tabRow) = createRefs()

        HorizontalPager(
            state = pagerState,
            pageSpacing = 0.dp,
            pageSize = PageSize.Fill,
            pageNestedScrollConnection =
                PagerDefaults.pageNestedScrollConnection(
                    Orientation.Horizontal
                ),
            pageContent = { index ->
                subScreens[index].screenContent()
            },
            key = { index ->
                subScreens[index].title
            },
            beyondBoundsPageCount = 1,
            modifier =
                Modifier.constrainAs(pager) {
                    top.linkTo(parent.top)
                    bottom.linkTo(tabRow.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )

        Column(
            modifier =
                Modifier.constrainAs(tabRow) {
                    top.linkTo(pager.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
        ) {
            Divider(
                thickness = DividerDefaults.Thickness,
                color = ZcashTheme.colors.dividerColor
            )
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                // Don't use the predefined divider, as its fixed position is below the tabs bar
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier =
                            Modifier
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                .padding(horizontal = ZcashTheme.dimens.spacingDefault),
                        color = ZcashTheme.colors.complementaryColor
                    )
                },
                modifier =
                    Modifier
                        .navigationBarsPadding()
                        .padding(
                            horizontal = ZcashTheme.dimens.spacingDefault,
                            vertical = ZcashTheme.dimens.spacingSmall
                        )
            ) {
                subScreens.forEachIndexed { index, item ->
                    val selected = index == pagerState.currentPage

                    NavigationTabText(
                        text = item.title,
                        selected = selected,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        modifier =
                            Modifier
                                .padding(
                                    horizontal = ZcashTheme.dimens.spacingXtiny,
                                    vertical = ZcashTheme.dimens.spacingDefault
                                )
                                .testTag(item.testTag)
                    )
                }
            }
        }
    }

    if (isKeepScreenOnWhileSyncing == true &&
        walletSnapshot?.status == Synchronizer.Status.SYNCING
    ) {
        DisableScreenTimeout()
    }
}
