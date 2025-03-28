@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.send

import android.content.pm.PackageManager
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.usecase.ObserveClearSendUseCase
import co.electriccoin.zcash.ui.common.usecase.PrefillSendUseCase
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.viewmodel.ZashiMainTopAppBarViewModel
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendArguments
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Locale

@Composable
@Suppress("LongParameterList")
internal fun WrapSend(
    sendArguments: SendArguments?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
) {
    val activity = LocalActivity.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val homeViewModel = koinActivityViewModel<HomeViewModel>()

    val hasCameraFeature = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.currentWalletSnapshot.collectAsStateWithLifecycle().value

    val monetarySeparators = MonetarySeparators.current(Locale.getDefault())

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    val isHideBalances = homeViewModel.isHideBalances.collectAsStateWithLifecycle().value ?: false

    val exchangeRateState = walletViewModel.exchangeRateUsd.collectAsStateWithLifecycle().value

    WrapSend(
        balanceState = balanceState,
        exchangeRateState = exchangeRateState,
        isHideBalances = isHideBalances,
        goToQrScanner = goToQrScanner,
        goBack = goBack,
        goBalances = goBalances,
        hasCameraFeature = hasCameraFeature,
        monetarySeparators = monetarySeparators,
        sendArguments = sendArguments,
        synchronizer = synchronizer,
        walletSnapshot = walletSnapshot
    )
}

@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@VisibleForTesting
@Composable
internal fun WrapSend(
    balanceState: BalanceState,
    exchangeRateState: ExchangeRateState,
    isHideBalances: Boolean,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    hasCameraFeature: Boolean,
    monetarySeparators: MonetarySeparators,
    sendArguments: SendArguments?,
    synchronizer: Synchronizer?,
    walletSnapshot: WalletSnapshot?,
) {
    val scope = rememberCoroutineScope()

    val navController = LocalNavController.current

    val viewModel = koinViewModel<SendViewModel>()

    LaunchedEffect(Unit) {
        viewModel.navigateCommand.collect {
            navController.navigate(it)
        }
    }

    val sendAddressBookState by viewModel.sendAddressBookState.collectAsStateWithLifecycle()

    val topAppBarViewModel = koinActivityViewModel<ZashiMainTopAppBarViewModel>()

    val zashiMainTopAppBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val (sendStage, setSendStage) =
        rememberSaveable(stateSaver = SendStage.Saver) { mutableStateOf(SendStage.Form) }

    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    val recipientAddressState by viewModel.recipientAddressState.collectAsStateWithLifecycle()

    val observeClearSend = koinInject<ObserveClearSendUseCase>()
    val prefillSend = koinInject<PrefillSendUseCase>()

    if (sendArguments?.recipientAddress != null) {
        viewModel.onRecipientAddressChanged(
            RecipientAddressState.new(
                sendArguments.recipientAddress.address,
                sendArguments.recipientAddress.type
            )
        )
    }

    // Zip321 Uri scan result processing
    if (sendArguments?.zip321Uri != null &&
        synchronizer != null
    ) {
        LaunchedEffect(Unit) {
            viewModel.onCreateZecSend321Click(
                zip321Uri = sendArguments.zip321Uri,
                setSendStage = setSendStage,
            )
        }
    }

    // Amount computation:
    val (amountState, setAmountState) =
        rememberSaveable(stateSaver = AmountState.Saver) {
            // Default amount state
            mutableStateOf(
                AmountState.newFromZec(
                    context = context,
                    value = zecSend?.amount?.toZecString() ?: "",
                    monetarySeparators = monetarySeparators,
                    isTransparentOrTextRecipient =
                        recipientAddressState.type?.let { it == AddressType.Transparent }
                            ?: false,
                    fiatValue = "",
                    exchangeRateState = exchangeRateState
                )
            )
        }
    // New amount state based on the recipient address type (e.g. shielded supports zero funds sending and
    // transparent not)
    LaunchedEffect(recipientAddressState, exchangeRateState) {
        setAmountState(
            if (amountState.value.isNotBlank() || amountState.fiatValue.isBlank()) {
                AmountState.newFromZec(
                    context = context,
                    isTransparentOrTextRecipient =
                        recipientAddressState.type
                            ?.let { it == AddressType.Transparent } ?: false,
                    monetarySeparators = monetarySeparators,
                    value = amountState.value,
                    fiatValue = amountState.fiatValue,
                    exchangeRateState = exchangeRateState
                )
            } else {
                AmountState.newFromFiat(
                    context = context,
                    isTransparentOrTextRecipient =
                        recipientAddressState.type
                            ?.let { it == AddressType.Transparent } ?: false,
                    monetarySeparators = monetarySeparators,
                    value = amountState.value,
                    fiatValue = amountState.fiatValue,
                    exchangeRateState = exchangeRateState
                )
            }
        )
    }

    // Memo computation:
    val (memoState, setMemoState) =
        rememberSaveable(stateSaver = MemoState.Saver) {
            mutableStateOf(MemoState.new(zecSend?.memo?.value ?: ""))
        }

    LaunchedEffect(Unit) {
        observeClearSend().collect {
            setSendStage(SendStage.Form)
            setZecSend(null)
            viewModel.onRecipientAddressChanged(RecipientAddressState.new("", null))
            setAmountState(
                AmountState.newFromZec(
                    context = context,
                    monetarySeparators = monetarySeparators,
                    value = "",
                    fiatValue = "",
                    isTransparentOrTextRecipient = false,
                    exchangeRateState = exchangeRateState
                )
            )
            setMemoState(MemoState.new(""))
        }
    }

    LaunchedEffect(Unit) {
        prefillSend().collect {
            val type = synchronizer?.validateAddress(it.recipientAddress?.address.orEmpty())
            setSendStage(SendStage.Form)
            setZecSend(null)
            viewModel.onRecipientAddressChanged(
                RecipientAddressState.new(
                    address = it.recipientAddress?.address.orEmpty(),
                    type = type
                )
            )

            val fee = it.transaction.fee
            val value = if (fee == null) it.transaction.amount else it.transaction.amount - fee

            setAmountState(
                AmountState.newFromZec(
                    context = context,
                    value = value.convertZatoshiToZecString(),
                    monetarySeparators = monetarySeparators,
                    isTransparentOrTextRecipient = type == AddressType.Transparent,
                    fiatValue = amountState.fiatValue,
                    exchangeRateState = exchangeRateState
                )
            )
            setMemoState(MemoState.new(it.memos?.firstOrNull().orEmpty()))
        }
    }

    // Clearing form from the previous navigation destination if required
    if (sendArguments?.clearForm == true) {
        setSendStage(SendStage.Form)
        setZecSend(null)
        viewModel.onRecipientAddressChanged(RecipientAddressState.new("", null))
        setAmountState(
            AmountState.newFromZec(
                context = context,
                monetarySeparators = monetarySeparators,
                value = "",
                fiatValue = "",
                isTransparentOrTextRecipient = false,
                exchangeRateState = exchangeRateState
            )
        )
        setMemoState(MemoState.new(""))
    }

    val onBackAction = {
        when (sendStage) {
            SendStage.Form -> goBack()
            SendStage.Proposing -> {
                // no action - wait until the sending is done
            }

            is SendStage.SendFailure -> setSendStage(SendStage.Form)
        }
    }

    if (null == synchronizer || null == walletSnapshot) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Send(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            sendStage = sendStage,
            onCreateZecSend = { newZecSend ->
                viewModel.onCreateZecSendClick(
                    newZecSend = newZecSend,
                    setSendStage = setSendStage
                )
            },
            onBack = onBackAction,
            recipientAddressState = recipientAddressState,
            onRecipientAddressChange = {
                scope.launch {
                    viewModel.onRecipientAddressChanged(
                        RecipientAddressState.new(
                            address = it,
                            // TODO [#342]: Verify Addresses without Synchronizer
                            // TODO [#342]: https://github.com/zcash/zcash-android-wallet-sdk/issues/342
                            type = synchronizer.validateAddress(it)
                        )
                    )
                }
            },
            memoState = memoState,
            setMemoState = setMemoState,
            amountState = amountState,
            setAmountState = setAmountState,
            onQrScannerOpen = goToQrScanner,
            goBalances = goBalances,
            hasCameraFeature = hasCameraFeature,
            walletSnapshot = walletSnapshot,
            exchangeRateState = exchangeRateState,
            sendAddressBookState = sendAddressBookState,
            zashiMainTopAppBarState = zashiMainTopAppBarState
        )
    }
}
