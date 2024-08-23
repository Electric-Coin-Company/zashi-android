@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.RestoreScreenBrightness
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.isSynced
import co.electriccoin.zcash.ui.screen.account.WrapAccount
import co.electriccoin.zcash.ui.screen.balances.WrapBalances
import co.electriccoin.zcash.ui.screen.home.model.TabItem
import co.electriccoin.zcash.ui.screen.home.view.Home
import co.electriccoin.zcash.ui.screen.receive.WrapReceive
import co.electriccoin.zcash.ui.screen.send.WrapSend
import co.electriccoin.zcash.ui.screen.send.model.SendArguments
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Suppress("LongParameterList")
internal fun WrapHome(
    goSettings: () -> Unit,
    goMultiTrxSubmissionFailure: () -> Unit,
    goScan: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    sendArguments: SendArguments
) {
    val activity = LocalActivity.current

    val homeViewModel by activity.viewModels<HomeViewModel>()

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val isKeepScreenOnWhileSyncing = homeViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    // Once the wallet is fully synced and still in restoring state, persist the new state
    if (walletSnapshot?.status?.isSynced() == true && walletRestoringState.isRunningRestoring()) {
        walletViewModel.persistWalletRestoringState(WalletRestoringState.SYNCING)
    }

    // TODO [#1523]: Refactor RestoreSuccess screen navigation
    // TODO [#1523]: https://github.com/Electric-Coin-Company/zashi-android/issues/1523
    val isRestoreSuccessSeen = homeViewModel.isRestoreSuccessSeen.collectAsStateWithLifecycle().value

    var isShowingRestoreSuccess by rememberSaveable { mutableStateOf(false) }
    val setShowingRestoreSuccess = {
        homeViewModel.setRestoringInitialWarningSeen()
        isShowingRestoreSuccess = false
    }

    // Show initial restore success screen
    isRestoreSuccessSeen?.let { restoreSuccessSeen ->
        if (!restoreSuccessSeen && walletRestoringState == WalletRestoringState.RESTORING) {
            LaunchedEffect(key1 = isShowingRestoreSuccess) {
                // Adding an extra little delay before displaying for a better UX
                @Suppress("MagicNumber")
                delay(1500)
                isShowingRestoreSuccess = true
            }
        }
    }

    WrapHome(
        goScan = goScan,
        goSendConfirmation = goSendConfirmation,
        goSettings = goSettings,
        goMultiTrxSubmissionFailure = goMultiTrxSubmissionFailure,
        isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
        isShowingRestoreSuccess = isShowingRestoreSuccess,
        sendArguments = sendArguments,
        setShowingRestoreSuccess = setShowingRestoreSuccess,
        walletSnapshot = walletSnapshot
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongParameterList", "LongMethod")
@Composable
internal fun WrapHome(
    goSettings: () -> Unit,
    goMultiTrxSubmissionFailure: () -> Unit,
    goScan: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    isKeepScreenOnWhileSyncing: Boolean?,
    isShowingRestoreSuccess: Boolean,
    sendArguments: SendArguments,
    setShowingRestoreSuccess: () -> Unit,
    walletSnapshot: WalletSnapshot?,
) {
    val activity = LocalActivity.current

    val focusManager = LocalFocusManager.current

    val walletViewModel by activity.viewModels<WalletViewModel>()

    val scope = rememberCoroutineScope()

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { 4 }
        )

    val homeGoBack: () -> Unit by remember(pagerState.currentPage, scope) {
        derivedStateOf {
            {
                when (pagerState.currentPage) {
                    HomeScreenIndex.ACCOUNT.pageIndex -> activity.finish()
                    HomeScreenIndex.SEND.pageIndex,
                    HomeScreenIndex.RECEIVE.pageIndex,
                    HomeScreenIndex.BALANCES.pageIndex ->
                        scope.launch {
                            pagerState.animateScrollToPage(HomeScreenIndex.ACCOUNT.pageIndex)
                        }
                }
            }
        }
    }

    BackHandler {
        homeGoBack()
    }

    // Reset the screen brightness for all pages except Receive which maintain the screen brightness by itself
    if (pagerState.currentPage != HomeScreenIndex.RECEIVE.pageIndex) {
        RestoreScreenBrightness()
    }

    val tabs =
        persistentListOf(
            TabItem(
                index = HomeScreenIndex.ACCOUNT,
                title = stringResource(id = R.string.home_tab_account),
                testTag = HomeTag.TAB_ACCOUNT,
                screenContent = {
                    WrapAccount(
                        goBalances = {
                            scope.launch {
                                pagerState.animateScrollToPage(HomeScreenIndex.BALANCES.pageIndex)
                            }
                        },
                        goSettings = goSettings
                    )
                }
            ),
            TabItem(
                index = HomeScreenIndex.SEND,
                title = stringResource(id = R.string.home_tab_send),
                testTag = HomeTag.TAB_SEND,
                screenContent = {
                    WrapSend(
                        goToQrScanner = goScan,
                        goBack = homeGoBack,
                        goBalances = {
                            scope.launch {
                                pagerState.animateScrollToPage(HomeScreenIndex.BALANCES.pageIndex)
                            }
                        },
                        goSendConfirmation = goSendConfirmation,
                        goSettings = goSettings,
                        sendArguments = sendArguments
                    )
                }
            ),
            TabItem(
                index = HomeScreenIndex.RECEIVE,
                title = stringResource(id = R.string.home_tab_receive),
                testTag = HomeTag.TAB_RECEIVE,
                screenContent = {
                    WrapReceive(onSettings = goSettings)
                }
            ),
            TabItem(
                index = HomeScreenIndex.BALANCES,
                title = stringResource(id = R.string.home_tab_balances),
                testTag = HomeTag.TAB_BALANCES,
                screenContent = {
                    WrapBalances(
                        goSettings = goSettings,
                        goMultiTrxSubmissionFailure = goMultiTrxSubmissionFailure
                    )
                }
            )
        )

    Home(
        subScreens = tabs,
        isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
        isShowingRestoreSuccess = isShowingRestoreSuccess,
        setShowingRestoreSuccess = setShowingRestoreSuccess,
        walletSnapshot = walletSnapshot,
        pagerState = pagerState,
    )

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == HomeScreenIndex.SEND.pageIndex) {
            walletViewModel.refreshExchangeRateUsd()
        } else {
            focusManager.clearFocus(true)
        }
    }
}

/**
 * Enum of the Home screen sub-screens
 */
@Suppress("MagicNumber")
enum class HomeScreenIndex(val pageIndex: Int) {
    ACCOUNT(0),
    SEND(1),
    RECEIVE(2),
    BALANCES(3)
}
