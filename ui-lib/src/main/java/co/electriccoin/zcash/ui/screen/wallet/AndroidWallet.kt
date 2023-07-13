package co.electriccoin.zcash.ui.screen.wallet

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.TransactionOverview
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.global.DeepLinkUtil
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.MIN_ZEC_FOR_SHIELDING
import co.electriccoin.zcash.ui.common.showMessage
import co.electriccoin.zcash.ui.common.toFormattedString
import co.electriccoin.zcash.ui.configuration.ConfigurationEntries
import co.electriccoin.zcash.ui.configuration.RemoteConfig
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.settings.viewmodel.SettingsViewModel
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUIState
import co.electriccoin.zcash.ui.screen.shield.model.ShieldUiDestination
import co.electriccoin.zcash.ui.screen.shield.viewmodel.ShieldViewModel
import co.electriccoin.zcash.ui.screen.wallet.view.WalletView
import co.electriccoin.zcash.ui.screen.wallet.view.isSyncing

@Composable
internal fun MainActivity.AndroidWallet(
    onAddressQrCodes: () -> Unit,
    onShieldNow: () -> Unit,
    onTransactionDetail: (Long) -> Unit,
    onViewTransactionHistory: () -> Unit,
    onSendFromDeepLink: () -> Unit
) {
    WrapWallet(
        activity = this,
        onAddressQrCodes = onAddressQrCodes,
        onShieldNow = onShieldNow,
        onTransactionDetail = onTransactionDetail,
        onViewTransactionHistory = onViewTransactionHistory,
        onSendFromDeepLink = onSendFromDeepLink
    )
}

@Composable
internal fun WrapWallet(
    activity: ComponentActivity,
    onAddressQrCodes: () -> Unit,
    onShieldNow: () -> Unit,
    onTransactionDetail: (Long) -> Unit,
    onViewTransactionHistory: () -> Unit,
    onSendFromDeepLink: () -> Unit
) {
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value
    val transactionSnapshot =
        walletViewModel.transactionSnapshot.collectAsStateWithLifecycle().value
    val shieldViewModel = viewModel<ShieldViewModel>()

    val settingsViewModel by activity.viewModels<SettingsViewModel>()
    val isKeepScreenOnWhileSyncing =
        settingsViewModel.isKeepScreenOnWhileSyncing.collectAsStateWithLifecycle().value
    val isFiatConversionEnabled =
        ConfigurationEntries.IS_FIAT_CONVERSION_ENABLED.getValue(RemoteConfig.current)
    val clipboardManager = LocalClipboardManager.current

    val isAutoShieldingInitiated = remember {
        mutableStateOf(false)
    }

    if (null == walletSnapshot) {
        // We can show progress bar
    } else {
        val isSyncing = isSyncing(walletSnapshot.status)
        LaunchedEffect(key1 = isSyncing) {
            homeViewModel.onTransferTabStateChanged(enable = isSyncing.not())

            if (isSyncing.not()) {
                homeViewModel.intentDataUriForDeepLink?.let {
                    DeepLinkUtil.getSendDeepLinkData(it)?.let { sendDeepLinkData ->
                        homeViewModel.sendDeepLinkData = sendDeepLinkData
                        onSendFromDeepLink()
                        homeViewModel.intentDataUriForDeepLink = null
                    }
                }
                checkForAutoShielding(walletSnapshot.transparentBalance.available, shieldViewModel)
                if (homeViewModel.isAnyExpectingTransaction(walletSnapshot)) {
                    activity.showMessage(activity.getString(R.string.ns_expecting_balance_snack_bar_msg,
                        Zatoshi(homeViewModel.expectingZatoshi).toZecString()
                    ))
                }
            }
        }
        val onItemLongClickAction: (TransactionOverview) -> Unit = {
            clipboardManager.setText(AnnotatedString(it.rawId.byteArray.toFormattedString()))
            activity.showMessage(activity.getString(R.string.transaction_id_copied))
        }
        WalletView(
            walletSnapshot = walletSnapshot,
            transactionSnapshot = transactionSnapshot,
            isKeepScreenOnWhileSyncing = isKeepScreenOnWhileSyncing,
            isFiatConversionEnabled = isFiatConversionEnabled,
            onShieldNow = onShieldNow,
            onAddressQrCodes = onAddressQrCodes,
            onTransactionDetail = onTransactionDetail,
            onViewTransactionHistory = onViewTransactionHistory,
            onLongItemClick = onItemLongClickAction
        )

        val shieldUIState = shieldViewModel.shieldUIState.collectAsStateWithLifecycle().value
        if (isEnoughBalanceForAutoShield(walletSnapshot.transparentBalance.available)) {
            ((shieldUIState is ShieldUIState.OnResult) && shieldUIState.destination == ShieldUiDestination.ShieldFunds)
                .let {
                    if (it && isAutoShieldingInitiated.value.not()) {
                        Twig.info { "AutoShield available" }
                        isAutoShieldingInitiated.value = true
                        shieldViewModel.clearData()
                        onShieldNow()
                    }
                }
        }
    }
    activity.reportFullyDrawn()
}

fun checkForAutoShielding(availableZatoshi: Zatoshi, shieldViewModel: ShieldViewModel) {
    if (isEnoughBalanceForAutoShield(availableZatoshi)) {
        shieldViewModel.checkAutoShieldUiState()
    }
}

fun isEnoughBalanceForAutoShield(availableZatoshi: Zatoshi) =
    availableZatoshi >= MIN_ZEC_FOR_SHIELDING.convertZecToZatoshi()
