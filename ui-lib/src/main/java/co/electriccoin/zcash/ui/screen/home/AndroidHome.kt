@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.account.WrapAccount
import co.electriccoin.zcash.ui.screen.balances.WrapBalances
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.receive.WrapReceive
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("LongParameterList")
@Composable
internal fun MainActivity.WrapHome(
    onPageChange: (HomeScreenIndex) -> Unit,
    goBack: () -> Unit,
    goHistory: () -> Unit,
    goSettings: () -> Unit,
    goScan: () -> Unit,
    sendArgumentsWrapper: SendArgumentsWrapper
) {
    WrapHome(
        this,
        onPageChange = onPageChange,
        goBack = goBack,
        goHistory = goHistory,
        goScan = goScan,
        goSettings = goSettings,
        sendArgumentsWrapper = sendArgumentsWrapper
    )
}

@Suppress("LongParameterList", "LongMethod")
@Composable
internal fun WrapHome(
    activity: ComponentActivity,
    goBack: () -> Unit,
    goHistory: () -> Unit,
    goSettings: () -> Unit,
    goScan: () -> Unit,
    onPageChange: (HomeScreenIndex) -> Unit,
    sendArgumentsWrapper: SendArgumentsWrapper
) {
    val homeViewModel by activity.viewModels<HomeViewModel>()

    // Flow for propagating the new page index to the pager in the view layer
    val forceHomePageIndexFlow: MutableSharedFlow<ForcePage?> =
        MutableSharedFlow(
            Int.MAX_VALUE,
            Int.MAX_VALUE,
            BufferOverflow.SUSPEND
        )
    val forceIndex = forceHomePageIndexFlow.collectAsState(initial = null).value

    val homeGoBack: () -> Unit = {
        when (homeViewModel.screenIndex.value) {
            HomeScreenIndex.ACCOUNT -> goBack()
            HomeScreenIndex.SEND,
            HomeScreenIndex.RECEIVE,
            HomeScreenIndex.BALANCES -> forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.ACCOUNT))
        }
    }

    BackHandler {
        homeGoBack()
    }

    val tabs =
        persistentListOf(
            TabItem(
                index = HomeScreenIndex.ACCOUNT,
                title = stringResource(id = R.string.home_tab_account),
                testTag = HomeTag.TAB_ACCOUNT,
                screenContent = {
                    WrapAccount(
                        activity = activity,
                        goBalances = { forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.BALANCES)) },
                        goHistory = goHistory,
                        goSettings = goSettings,
                    )
                }
            ),
            TabItem(
                index = HomeScreenIndex.SEND,
                title = stringResource(id = R.string.home_tab_send),
                testTag = HomeTag.TAB_SEND,
                screenContent = {
                    WrapSend(
                        activity = activity,
                        goToQrScanner = goScan,
                        goBack = homeGoBack,
                        goBalances = { forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.BALANCES)) },
                        goSettings = goSettings,
                        sendArgumentsWrapper = sendArgumentsWrapper
                    )
                }
            ),
            TabItem(
                index = HomeScreenIndex.RECEIVE,
                title = stringResource(id = R.string.home_tab_receive),
                testTag = HomeTag.TAB_RECEIVE,
                screenContent = {
                    WrapReceive(
                        activity = activity,
                        onSettings = goSettings,
                    )
                }
            ),
            TabItem(
                index = HomeScreenIndex.BALANCES,
                title = stringResource(id = R.string.home_tab_balances),
                testTag = HomeTag.TAB_BALANCES,
                screenContent = {
                    WrapBalances(
                        activity = activity,
                        goSettings = goSettings
                    )
                }
            )
        )

    Home(
        subScreens = tabs,
        forcePage = forceIndex,
        onPageChange = onPageChange
    )
}

/**
 * Wrapper class used to pass forced pages index into the view layer
 */
class ForcePage(
    val currentPage: HomeScreenIndex,
)

/**
 * Enum of the Home screen sub-screens
 */
enum class HomeScreenIndex {
    // WARN: Be careful when re-ordering these, as the ordinal number states for their order
    ACCOUNT,
    SEND,
    RECEIVE,
    BALANCES, ;

    companion object {
        fun fromIndex(index: Int) = entries[index]
    }
}
