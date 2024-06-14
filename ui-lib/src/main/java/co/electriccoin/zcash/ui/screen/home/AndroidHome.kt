@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.model.ZecSend
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
@Suppress("LongParameterList")
internal fun MainActivity.WrapHome(
    goBack: () -> Unit,
    goSettings: () -> Unit,
    goMultiTrxSubmissionFailure: () -> Unit,
    goScan: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    sendArguments: SendArguments
) {
    val homeViewModel by viewModels<HomeViewModel>()

    val walletViewModel by viewModels<WalletViewModel>()

    val homeScreenIndex = homeViewModel.screenIndex.collectAsStateWithLifecycle().value

    val isKeepScreenOnWhileSyncing = homeViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value

    val isRestoringInitialWarningSeen = homeViewModel.isRestoringInitialWarningSeen.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    // Once the wallet is fully synced and still in restoring state, persist the new state
    if (walletSnapshot?.status?.isSynced() == true && walletRestoringState.isRunningRestoring()) {
        walletViewModel.persistWalletRestoringState(WalletRestoringState.SYNCING)
    }

    var isShowingRestoreInitDialog by rememberSaveable { mutableStateOf(false) }
    val setShowingRestoreInitDialog = {
        homeViewModel.setRestoringInitialWarningSeen()
        isShowingRestoreInitDialog = false
    }

    // Show initial restoring warn dialog
    isRestoringInitialWarningSeen?.let { restoringWarningSeen ->
        if (!restoringWarningSeen && walletRestoringState == WalletRestoringState.RESTORING) {
            LaunchedEffect(key1 = isShowingRestoreInitDialog) {
                // Adding an extra little delay before displaying the dialog for a better UX
                @Suppress("MagicNumber")
                delay(1500)
                isShowingRestoreInitDialog = true
            }
        }
    }

    WrapHome(
        this,
        goBack = goBack,
        goScan = goScan,
        goSendConfirmation = goSendConfirmation,
        goSettings = goSettings,
        goMultiTrxSubmissionFailure = goMultiTrxSubmissionFailure,
        homeScreenIndex = homeScreenIndex,
        isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
        isShowingRestoreInitDialog = isShowingRestoreInitDialog,
        onPageChange = {
            homeViewModel.screenIndex.value = it
        },
        sendArguments = sendArguments,
        setShowingRestoreInitDialog = setShowingRestoreInitDialog,
        walletSnapshot = walletSnapshot
    )
}

@Suppress("LongParameterList", "LongMethod")
@Composable
internal fun WrapHome(
    activity: MainActivity,
    goBack: () -> Unit,
    goSettings: () -> Unit,
    goMultiTrxSubmissionFailure: () -> Unit,
    goScan: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    homeScreenIndex: HomeScreenIndex,
    isKeepScreenOnWhileSyncing: Boolean?,
    isShowingRestoreInitDialog: Boolean,
    onPageChange: (HomeScreenIndex) -> Unit,
    sendArguments: SendArguments,
    setShowingRestoreInitDialog: () -> Unit,
    walletSnapshot: WalletSnapshot?,
) {
    // Flow for propagating the new page index to the pager in the view layer
    val forceHomePageIndexFlow: MutableSharedFlow<ForcePage?> =
        MutableSharedFlow(
            Int.MAX_VALUE,
            Int.MAX_VALUE,
            BufferOverflow.SUSPEND
        )
    val forceIndex = forceHomePageIndexFlow.collectAsState(initial = null).value

    val homeGoBack: () -> Unit = {
        when (homeScreenIndex) {
            HomeScreenIndex.ACCOUNT -> goBack()
            HomeScreenIndex.SEND,
            HomeScreenIndex.RECEIVE,
            HomeScreenIndex.BALANCES -> forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.ACCOUNT))
        }
    }

    BackHandler {
        homeGoBack()
    }

    // Reset the screen brightness for all pages except Receive which maintain the screen brightness by itself
    if (homeScreenIndex != HomeScreenIndex.RECEIVE) {
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
                        activity = activity,
                        goBalances = { forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.BALANCES)) },
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
                        activity = activity,
                        goToQrScanner = goScan,
                        goBack = homeGoBack,
                        goBalances = { forceHomePageIndexFlow.tryEmit(ForcePage(HomeScreenIndex.BALANCES)) },
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
                    WrapReceive(
                        activity = activity,
                        onSettings = goSettings
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
                        goSettings = goSettings,
                        goMultiTrxSubmissionFailure = goMultiTrxSubmissionFailure
                    )
                }
            )
        )

    Home(
        subScreens = tabs,
        forcePage = forceIndex,
        isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
        isShowingRestoreInitDialog = isShowingRestoreInitDialog,
        onPageChange = onPageChange,
        setShowingRestoreInitDialog = setShowingRestoreInitDialog,
        walletSnapshot = walletSnapshot
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
