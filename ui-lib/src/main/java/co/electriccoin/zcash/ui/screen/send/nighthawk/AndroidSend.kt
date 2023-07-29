package co.electriccoin.zcash.ui.screen.send.nighthawk

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSendExt
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.navigation.BottomNavItem
import co.electriccoin.zcash.ui.screen.send.ext.ABBREVIATION_INDEX
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.SendUIState
import co.electriccoin.zcash.ui.screen.send.nighthawk.view.EnterMessage
import co.electriccoin.zcash.ui.screen.send.nighthawk.view.EnterReceiverAddress
import co.electriccoin.zcash.ui.screen.send.nighthawk.view.EnterZec
import co.electriccoin.zcash.ui.screen.send.nighthawk.view.ReviewAndSend
import co.electriccoin.zcash.ui.screen.send.nighthawk.view.SendConfirmation
import co.electriccoin.zcash.ui.screen.send.nighthawk.viewmodel.SendViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.AndroidSend(
    onBack: () -> Unit,
    onTopUpWallet: () -> Unit,
    navigateTo: (String) -> Unit,
    onMoreDetails: (Long) -> Unit,
    onScan: () -> Unit,
    sendArgumentsWrapper: SendArgumentsWrapper? = null
) {
    WrapAndroidSend(
        activity = this,
        onBack = onBack,
        onTopUpWallet = onTopUpWallet,
        navigateTo = navigateTo,
        onMoreDetails = onMoreDetails,
        onScan = onScan,
        sendArgumentsWrapper = sendArgumentsWrapper
    )
}

@Composable
internal fun WrapAndroidSend(
    activity: ComponentActivity,
    onBack: () -> Unit,
    onTopUpWallet: () -> Unit,
    navigateTo: (String) -> Unit,
    onMoreDetails: (Long) -> Unit,
    onScan: () -> Unit,
    sendArgumentsWrapper: SendArgumentsWrapper? = null
) {
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val sendViewModel by activity.viewModels<SendViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val sendUIState = sendViewModel.currentSendUIState.collectAsStateWithLifecycle()
    BackHandler(enabled = sendUIState.value != SendUIState.ENTER_ZEC) {
        Twig.info { "WrapAndroidSend BackHandler: sendUIState $sendUIState" }
    }
    DisposableEffect(key1 = Unit) {
        // Check for deepLink data if there is any. If we found then update receiverAddress, amount and memo
        homeViewModel.sendDeepLinkData?.let {
            sendViewModel.updateReceiverAddress(it.address)
            it.amount?.let { zatoshi -> sendViewModel.enteredZecFromDeepLink(Zatoshi(zatoshi).convertZatoshiToZecString()) }
            it.memo?.let { memo -> sendViewModel.updateMemo(memo) }
        }?.also { homeViewModel.sendDeepLinkData = null }

        // Get data after scan the QR code and update sendViewModel receiver address
        sendArgumentsWrapper?.let {
            it.recipientAddress?.let { address ->
                sendViewModel.updateReceiverAddress(address)
            }
        }

        onDispose {
            Twig.info { "WrapAndroidSend: onDispose $sendUIState" }
        }
    }

    when (sendUIState.value) {
        SendUIState.ENTER_ZEC -> {
            walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.let(sendViewModel::updateEnterZecUiStateWithWalletSnapshot)
            val enterZecUIState = sendViewModel.enterZecUIState.collectAsStateWithLifecycle()
            EnterZec(
                enterZecUIState = enterZecUIState.value,
                onBack = onBack,
                onScanPaymentCode = onScan,
                onContinue = sendViewModel::onNextSendUiState,
                onTopUpWallet = onTopUpWallet,
                onKeyPressed = sendViewModel::onKeyPressed,
                onSendAllClicked = sendViewModel::onSendAllClicked
            )
        }

        SendUIState.ENTER_MESSAGE -> {
            EnterMessage(
                memo = sendViewModel.userEnteredMemo,
                onBack = sendViewModel::onPreviousSendUiState,
                onContinue = sendViewModel::onEnterMessageContinue
            )
        }

        SendUIState.ENTER_ADDRESS -> {
            val scope = rememberCoroutineScope()
            val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
            var isContinueEnabled by remember {
                mutableStateOf(false)
            }
            var validateAddressJob: Job? = null

            fun validateAddress(address: String) {
                validateAddressJob?.let {
                    if (it.isCompleted.not() && it.isCancelled.not()) {
                        it.cancel()
                    }
                }
                validateAddressJob = scope.launch(Dispatchers.IO) {
                    synchronizer?.let {
                        isContinueEnabled =
                            sendViewModel.validateAddress(address, it).isNotValid.not()
                    }
                }
            }

            LaunchedEffect(key1 = Unit) {
                validateAddress(sendViewModel.receiverAddress)
            }

            EnterReceiverAddress(
                receiverAddress = sendArgumentsWrapper?.recipientAddress ?: sendViewModel.receiverAddress,
                isContinueBtnEnabled = isContinueEnabled,
                onBack = sendViewModel::onPreviousSendUiState,
                onScan = onScan,
                onValueChanged = { address ->
                    if (address.length <= ABBREVIATION_INDEX) {
                        isContinueEnabled = false
                        return@EnterReceiverAddress
                    }
                    validateAddress(address)
                },
                onContinue = {
                    val zecSendValidation = ZecSendExt.new(
                        activity,
                        it,
                        sendViewModel.enterZecUIState.value.enteredAmount,
                        sendViewModel.userEnteredMemo,
                        MonetarySeparators.current().copy(decimal = '.')
                    )

                    when (zecSendValidation) {
                        is ZecSendExt.ZecSendValidation.Valid -> sendViewModel.onEnterReceiverAddressContinue(
                            it,
                            zecSendValidation.zecSend
                        )

                        is ZecSendExt.ZecSendValidation.Invalid -> {
                            Twig.error { "Error in onContinue after adding address ${zecSendValidation.validationErrors}" }
                            Toast.makeText(activity, "Error in validation", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            )
        }

        SendUIState.REVIEW_AND_SEND -> {
            val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
            val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value
            ReviewAndSend(
                sendAndReviewUiState = sendViewModel.sendAndReviewUiState(),
                onBack = sendViewModel::onPreviousSendUiState,
                onSendZCash = {
                    sendViewModel.onSendZCash(sendViewModel.zecSend, spendingKey, synchronizer)
                }
            )
        }

        SendUIState.SEND_CONFIRMATION -> {
            SendConfirmation(
                sendConfirmationState = sendViewModel.sendConfirmationState.collectAsStateWithLifecycle().value,
                onCancel = {
                    sendViewModel.clearViewModelSavedData()
                    navigateTo(BottomNavItem.Transfer.route)

                },
                onTryAgain = sendViewModel::onPreviousSendUiState,
                onDone = {
                    sendViewModel.clearViewModelSavedData()
                    navigateTo(BottomNavItem.Transfer.route)
                },
                onMoreDetails = {
                    sendViewModel.clearViewModelSavedData()
                    onMoreDetails(it)
                }
            )
        }

        null -> onBack.invoke()
    }
}
