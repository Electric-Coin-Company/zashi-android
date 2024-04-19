@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.send

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendArguments
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
@Suppress("LongParameterList")
internal fun WrapSend(
    activity: ComponentActivity,
    sendArguments: SendArguments?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    goSettings: () -> Unit,
) {
    val walletViewModel by activity.viewModels<WalletViewModel>()

    val hasCameraFeature = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    val homeViewModel by activity.viewModels<HomeViewModel>()

    val focusManager = LocalFocusManager.current

    if (homeViewModel.screenIndex.collectAsStateWithLifecycle().value != HomeScreenIndex.SEND) {
        // Clear focus on Send Form text fields
        focusManager.clearFocus(true)
    }

    // TODO [#1171]: Remove default MonetarySeparators locale
    // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
    val monetarySeparators = MonetarySeparators.current(Locale.US)

    val walletRestoringState = walletViewModel.walletRestoringState.collectAsStateWithLifecycle().value

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    WrapSend(
        balanceState = balanceState,
        sendArguments = sendArguments,
        synchronizer = synchronizer,
        walletSnapshot = walletSnapshot,
        spendingKey = spendingKey,
        focusManager = focusManager,
        goToQrScanner = goToQrScanner,
        goBack = goBack,
        goBalances = goBalances,
        goSettings = goSettings,
        goSendConfirmation = goSendConfirmation,
        hasCameraFeature = hasCameraFeature,
        monetarySeparators = monetarySeparators,
        walletRestoringState = walletRestoringState
    )
}

@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@VisibleForTesting
@Composable
internal fun WrapSend(
    balanceState: BalanceState,
    sendArguments: SendArguments?,
    synchronizer: Synchronizer?,
    walletSnapshot: WalletSnapshot?,
    spendingKey: UnifiedSpendingKey?,
    focusManager: FocusManager,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    hasCameraFeature: Boolean,
    monetarySeparators: MonetarySeparators,
    walletRestoringState: WalletRestoringState,
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val (sendStage, setSendStage) =
        rememberSaveable(stateSaver = SendStage.Saver) { mutableStateOf(SendStage.Form) }

    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    // Address computation:
    val (recipientAddressState, setRecipientAddressState) =
        rememberSaveable(stateSaver = RecipientAddressState.Saver) {
            mutableStateOf(RecipientAddressState.new(zecSend?.destination?.address ?: "", null))
        }
    if (sendArguments?.recipientAddress != null) {
        setRecipientAddressState(
            RecipientAddressState.new(
                sendArguments.recipientAddress.address,
                sendArguments.recipientAddress.type
            )
        )
    }

    // Amount computation:
    val (amountState, setAmountState) =
        rememberSaveable(stateSaver = AmountState.Saver) {
            // Default amount state
            mutableStateOf(
                AmountState.new(
                    context = context,
                    value = zecSend?.amount?.toZecString() ?: "",
                    monetarySeparators = monetarySeparators,
                    isTransparentRecipient = recipientAddressState.type?.let { it == AddressType.Transparent } ?: false
                )
            )
        }
    // New amount state based on the recipient address type (e.g. shielded supports zero funds sending and
    // transparent not)
    LaunchedEffect(key1 = recipientAddressState) {
        setAmountState(
            AmountState.new(
                context = context,
                isTransparentRecipient = recipientAddressState.type?.let { it == AddressType.Transparent } ?: false,
                monetarySeparators = monetarySeparators,
                value = amountState.value
            )
        )
    }

    // Memo computation:
    val (memoState, setMemoState) =
        rememberSaveable(stateSaver = MemoState.Saver) {
            mutableStateOf(MemoState.new(zecSend?.memo?.value ?: ""))
        }

    // Clearing form from the previous navigation destination if required
    if (sendArguments?.clearForm == true) {
        setSendStage(SendStage.Form)
        setZecSend(null)
        setRecipientAddressState(RecipientAddressState.new("", null))
        setAmountState(AmountState.new(context, monetarySeparators, "", false))
        setMemoState(MemoState.new(""))
    }

    val onBackAction = {
        when (sendStage) {
            SendStage.Form -> goBack()
            SendStage.Proposing -> { /* no action - wait until the sending is done */ }
            is SendStage.SendFailure -> setSendStage(SendStage.Form)
        }
    }

    BackHandler {
        onBackAction()
    }

    if (null == synchronizer || null == walletSnapshot || null == spendingKey) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Send(
            balanceState = balanceState,
            sendStage = sendStage,
            onCreateZecSend = { newZecSend ->
                scope.launch {
                    Twig.debug { "Getting send transaction proposal" }
                    runCatching {
                        synchronizer.proposeSend(spendingKey.account, newZecSend)
                    }.onSuccess { proposal ->
                        Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                        val enrichedZecSend = newZecSend.copy(proposal = proposal)
                        setZecSend(enrichedZecSend)
                        goSendConfirmation(enrichedZecSend)
                    }.onFailure {
                        Twig.error(it) { "Transaction proposal failed" }
                        setSendStage(SendStage.SendFailure(it.message ?: ""))
                    }
                }
            },
            focusManager = focusManager,
            onBack = onBackAction,
            onSettings = goSettings,
            recipientAddressState = recipientAddressState,
            onRecipientAddressChange = {
                scope.launch {
                    setRecipientAddressState(
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
            walletRestoringState = walletRestoringState,
            walletSnapshot = walletSnapshot,
        )
    }
}
