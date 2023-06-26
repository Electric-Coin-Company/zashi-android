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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.ext.ZcashSdk
import cash.z.ecc.android.sdk.ext.isShielded
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.send
import cash.z.ecc.android.sdk.model.toZecString
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.navigation.BottomNavItem
import co.electriccoin.zcash.ui.screen.send.ext.ABBREVIATION_INDEX
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.SendAndReviewUiState
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.SendConfirmationState
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
internal fun MainActivity.AndroidSend(onBack: () -> Unit, onTopUpWallet: () -> Unit, navigateTo: (String) -> Unit, onMoreDetails: (Long) -> Unit, onScan: () -> Unit) {
    WrapAndroidSend(activity = this, onBack = onBack, onTopUpWallet = onTopUpWallet, navigateTo = navigateTo, onMoreDetails = onMoreDetails, onScan = onScan)
}

@Composable
internal fun WrapAndroidSend(activity: ComponentActivity, onBack: () -> Unit, onTopUpWallet: () -> Unit, navigateTo: (String) -> Unit, onMoreDetails: (Long) -> Unit, onScan: () -> Unit) {
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val sendViewModel by activity.viewModels<SendViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val showBottomBarOnDispose = remember {
        mutableStateOf(true)
    }

    val sendUIState = sendViewModel.currentSendUIState.collectAsStateWithLifecycle()
    BackHandler(enabled = sendUIState.value != SendUIState.ENTER_ZEC) {
        Twig.info { "WrapAndroidSend BackHandler: sendUIState $sendUIState" }
    }
    DisposableEffect(key1 = Unit) {
        homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        showBottomBarOnDispose.value = true
        onDispose {
            Twig.info { "WrapAndroidSend: onDispose $sendUIState" }
            homeViewModel.onBottomNavBarVisibilityChanged(show = showBottomBarOnDispose.value)
        }
    }

    when (sendUIState.value) {
        SendUIState.ENTER_ZEC -> {
            walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value?.let(sendViewModel::updateEnterZecUiStateWithWalletSnapshot)
            val enterZecUIState = sendViewModel.enterZecUIState.collectAsStateWithLifecycle()
            EnterZec(
                enterZecUIState = enterZecUIState.value,
                onBack = onBack,
                onScanPaymentCode = {
                    showBottomBarOnDispose.value = false
                    onScan.invoke()
                },
                onContinue = sendViewModel::onNextSendUiState,
                onTopUpWallet = onTopUpWallet,
                onNotEnoughZCash = {
                    sendViewModel.clearViewModelSavedData()
                    onBack()
                },
                onKeyPressed = sendViewModel::onKeyPressed
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
                        isContinueEnabled = sendViewModel.validateAddress(address, it).isNotValid.not()
                    }
                }
            }

            LaunchedEffect(key1 = Unit) {
                validateAddress(sendViewModel.receiverAddress)
            }

            EnterReceiverAddress(
                receiverAddress = sendViewModel.receiverAddress,
                isContinueBtnEnabled = isContinueEnabled,
                onBack = sendViewModel::onPreviousSendUiState,
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
                        is ZecSendExt.ZecSendValidation.Valid -> sendViewModel.onEnterReceiverAddressContinue(it, zecSendValidation.zecSend)
                        is ZecSendExt.ZecSendValidation.Invalid -> {
                            Twig.error { "Error in onContinue after adding address ${zecSendValidation.validationErrors}" }
                            Toast.makeText(activity, "Error in validation", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        SendUIState.REVIEW_AND_SEND -> {
            val scope = rememberCoroutineScope()
            val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value
            val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value
            ReviewAndSend(
                sendAndReviewUiState = SendAndReviewUiState()
                    .copy(
                        amountToSend = sendViewModel.zecSend?.amount?.toZecString() ?: "",
                        convertedAmountWithCurrency = "--",
                        memo = sendViewModel.zecSend?.memo?.value ?: "",
                        recipientType = if ((sendViewModel.zecSend?.destination?.address
                                ?: "").isShielded())
                            stringResource(id = R.string.ns_shielded) else stringResource(id = R.string.ns_transparent),
                        receiverAddress = sendViewModel.zecSend?.destination?.address ?: "",
                        subTotal = sendViewModel.zecSend?.amount?.toZecString() ?: "",
                        networkFees = "${ZcashSdk.MINERS_FEE.value}",
                        totalAmount = "${sendViewModel.zecSend?.amount?.plus(ZcashSdk.MINERS_FEE)?.toZecString()}"
                    ),
                onBack = sendViewModel::onPreviousSendUiState,
                onSendZCash = {
                    sendViewModel.onSendZCash()
                    scope.launch {
                        val zecSend = sendViewModel.zecSend
                        if (zecSend == null) {
                            Twig.error { "Sending Zec: Send zec is null" }
                            sendViewModel.updateSendConfirmationState(SendConfirmationState.Failed)
                            return@launch
                        }
                        if (spendingKey == null) {
                            Twig.error { "Sending Zec: spending key is null" }
                            sendViewModel.updateSendConfirmationState(SendConfirmationState.Failed)
                            return@launch
                        }
                        if (synchronizer == null) {
                            Twig.error { "Sending Zec: synchronizer is null" }
                            sendViewModel.updateSendConfirmationState(SendConfirmationState.Failed)
                            return@launch
                        }
                        runCatching {
                            synchronizer.send(spendingKey = spendingKey, send = zecSend)
                        }
                            .onSuccess {
                                Twig.info { "Sending Zec: Sent successfully $it" }
                                sendViewModel.updateSendConfirmationState(SendConfirmationState.Success(it))
                            }
                            .onFailure {
                                Twig.error { "Sending Zec: Send fail $it" }
                                sendViewModel.updateSendConfirmationState(SendConfirmationState.Failed)
                            }
                    }
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
