@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.send

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
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
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
@Suppress("LongParameterList")
internal fun WrapSend(
    activity: ComponentActivity,
    sendArgumentsWrapper: SendArgumentsWrapper?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
) {
    val hasCameraFeature = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    val walletViewModel by activity.viewModels<WalletViewModel>()

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

    WrapSend(
        sendArgumentsWrapper,
        synchronizer,
        walletSnapshot,
        spendingKey,
        focusManager,
        goToQrScanner,
        goBack,
        goBalances,
        goSettings,
        hasCameraFeature,
        monetarySeparators
    )
}

@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@VisibleForTesting
@Composable
internal fun WrapSend(
    sendArgumentsWrapper: SendArgumentsWrapper?,
    synchronizer: Synchronizer?,
    walletSnapshot: WalletSnapshot?,
    spendingKey: UnifiedSpendingKey?,
    focusManager: FocusManager,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    goSettings: () -> Unit,
    hasCameraFeature: Boolean,
    monetarySeparators: MonetarySeparators
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    // For now, we're avoiding sub-navigation to keep the navigation logic simple.  But this might
    // change once deep-linking support  is added.  It depends on whether deep linking should do one of:
    // 1. Use a different UI flow entirely
    // 2. Show a pre-filled Send form
    // 3. Go directly to the Confirmation screen
    val (sendStage, setSendStage) =
        rememberSaveable(stateSaver = SendStage.Saver) { mutableStateOf(SendStage.Form) }

    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    // Address computation:
    val (recipientAddressState, setRecipientAddressState) =
        rememberSaveable(stateSaver = RecipientAddressState.Saver) {
            mutableStateOf(RecipientAddressState(zecSend?.destination?.address ?: "", null))
        }
    if (sendArgumentsWrapper?.recipientAddress != null) {
        setRecipientAddressState(
            RecipientAddressState.new(
                sendArgumentsWrapper.recipientAddress.address,
                sendArgumentsWrapper.recipientAddress.type
            )
        )
    }

    // Amount computation:
    val (amountState, setAmountState) =
        rememberSaveable(stateSaver = AmountState.Saver) {
            mutableStateOf(
                AmountState.new(
                    context = context,
                    value = zecSend?.amount?.toZecString() ?: "",
                    monetarySeparators = monetarySeparators
                )
            )
        }
    if (sendArgumentsWrapper?.amount != null) {
        setAmountState(
            AmountState.new(
                context = context,
                value = sendArgumentsWrapper.amount,
                monetarySeparators = monetarySeparators
            )
        )
    }

    // Memo computation:
    val (memoState, setMemoState) =
        rememberSaveable(stateSaver = MemoState.Saver) {
            mutableStateOf(MemoState.new(zecSend?.memo?.value ?: ""))
        }
    if (sendArgumentsWrapper?.memo != null) {
        setMemoState(MemoState.new(sendArgumentsWrapper.memo))
    }

    val onBackAction = {
        when (sendStage) {
            SendStage.Form -> goBack()
            SendStage.Confirmation -> setSendStage(SendStage.Form)
            SendStage.Sending -> { /* no action - wait until the sending is done */ }
            is SendStage.SendFailure -> setSendStage(SendStage.Form)
            SendStage.SendSuccessful -> {
                // Reset Send.Form values
                setZecSend(null)
                setRecipientAddressState(RecipientAddressState.new(""))
                setAmountState(AmountState.new(context, "", monetarySeparators))
                setMemoState(MemoState.new(""))

                setSendStage(SendStage.Form)
                goBack()
            }
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
            walletSnapshot = walletSnapshot,
            sendStage = sendStage,
            onSendStageChange = setSendStage,
            zecSend = zecSend,
            onCreateZecSend = { newZecSend ->
                scope.launch {
                    Twig.debug { "Getting send transaction proposal" }
                    runCatching {
                        synchronizer.proposeSend(spendingKey.account, newZecSend)
                    }
                        .onSuccess { proposal ->
                            Twig.debug { "Transaction proposal successful: ${proposal.toPrettyString()}" }
                            setSendStage(SendStage.Confirmation)
                            setZecSend(newZecSend.copy(proposal = proposal))
                        }
                        .onFailure {
                            Twig.error(it) { "Transaction proposal failed" }
                            // TODO [#1161]: Remove Send-Success and rework Send-Failure
                            // TODO [#1161]: https://github.com/Electric-Coin-Company/zashi-android/issues/1161
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
            onCreateAndSend = { newZecSend ->
                scope.launch {
                    Twig.debug { "Sending transaction" }
                    // TODO [#1294]: Add Send.Multiple-Trx-Failed screen
                    // TODO [#1294]: Note that the following processing is not entirely correct and will be reworked
                    // TODO [#1294]: https://github.com/Electric-Coin-Company/zashi-android/issues/1294
                    runCatching {
                        // The not-null assertion operator is necessary here even if we check its nullability before
                        // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
                        // property declared in different module
                        // See more details on the Kotlin forum
                        checkNotNull(newZecSend.proposal)
                        synchronizer.createProposedTransactions(newZecSend.proposal!!, spendingKey)
                    }
                        .onSuccess {
                            setSendStage(SendStage.SendSuccessful)
                            Twig.debug { "Transaction id:$it submitted successfully" }
                        }
                        .onFailure {
                            Twig.error(it) { "Transaction submission failed" }
                            setSendStage(SendStage.SendFailure(it.message ?: ""))
                        }
                }
            },
            memoState = memoState,
            setMemoState = setMemoState,
            amountState = amountState,
            setAmountState = setAmountState,
            onQrScannerOpen = goToQrScanner,
            goBalances = goBalances,
            hasCameraFeature = hasCameraFeature
        )
    }
}
