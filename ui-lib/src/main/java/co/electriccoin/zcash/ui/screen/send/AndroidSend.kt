@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.send

import android.content.pm.PackageManager
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Account
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.proposeSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.compose.LocalNavController
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.usecase.ObserveContactByAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveContactPickedUseCase
import co.electriccoin.zcash.ui.common.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.addressbook.AddressBookArgs
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
import co.electriccoin.zcash.ui.screen.send.model.SendArguments
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.zecdev.zip321.ZIP321
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
@Suppress("LongParameterList")
internal fun WrapSend(
    sendArguments: SendArguments?,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    goBalances: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    goPaymentRequest: (ZecSend, String) -> Unit,
    goSettings: () -> Unit,
) {
    val activity = LocalActivity.current

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val homeViewModel = koinActivityViewModel<HomeViewModel>()

    val hasCameraFeature = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val walletSnapshot = walletViewModel.walletSnapshot.collectAsStateWithLifecycle().value

    val spendingKey = walletViewModel.spendingKey.collectAsStateWithLifecycle().value

    val monetarySeparators = MonetarySeparators.current(Locale.getDefault())

    val walletState = walletViewModel.walletStateInformation.collectAsStateWithLifecycle().value

    val balanceState = walletViewModel.balanceState.collectAsStateWithLifecycle().value

    val isHideBalances = homeViewModel.isHideBalances.collectAsStateWithLifecycle().value ?: false

    val exchangeRateState = walletViewModel.exchangeRateUsd.collectAsStateWithLifecycle().value

    WrapSend(
        balanceState = balanceState,
        isHideBalances = isHideBalances,
        onHideBalances = { homeViewModel.showOrHideBalances() },
        sendArguments = sendArguments,
        synchronizer = synchronizer,
        walletSnapshot = walletSnapshot,
        spendingKey = spendingKey,
        goToQrScanner = goToQrScanner,
        goBack = goBack,
        goBalances = goBalances,
        goSettings = goSettings,
        goSendConfirmation = goSendConfirmation,
        goPaymentRequest = goPaymentRequest,
        hasCameraFeature = hasCameraFeature,
        monetarySeparators = monetarySeparators,
        topAppBarSubTitleState = walletState,
        exchangeRateState = exchangeRateState,
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
    goSettings: () -> Unit,
    goSendConfirmation: (ZecSend) -> Unit,
    goPaymentRequest: (ZecSend, String) -> Unit,
    hasCameraFeature: Boolean,
    monetarySeparators: MonetarySeparators,
    onHideBalances: () -> Unit,
    sendArguments: SendArguments?,
    spendingKey: UnifiedSpendingKey?,
    synchronizer: Synchronizer?,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    walletSnapshot: WalletSnapshot?,
) {
    val scope = rememberCoroutineScope()

    val navController = LocalNavController.current

    val observeContactByAddress = koinInject<ObserveContactByAddressUseCase>()
    val observeContactPicked = koinInject<ObserveContactPickedUseCase>()

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

    // Zip321 Uri scan result processing
    if (sendArguments?.zip321Uri != null) {
        if (synchronizer != null && spendingKey != null) {
            LaunchedEffect(Unit) {
                scope.launch {
                    processZip321Result(
                        zip321Uri = sendArguments.zip321Uri,
                        synchronizer = synchronizer,
                        account = spendingKey.account,
                        setSendStage = setSendStage,
                        setZecSend = setZecSend,
                        goPaymentRequest = goPaymentRequest
                    )
                }
            }
        }
    }

    val existingContact: MutableState<AddressBookContact?> = remember { mutableStateOf(null) }
    var isHintVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        observeContactPicked().collect {
            setRecipientAddressState(it)
        }
    }

    LaunchedEffect(recipientAddressState.address) {
        observeContactByAddress(recipientAddressState.address).collect {
            existingContact.value = it
        }
    }

    LaunchedEffect(existingContact, recipientAddressState.type) {
        val exists = existingContact.value != null
        val isValid = recipientAddressState.type?.isNotValid == false

        if (!exists && isValid) {
            isHintVisible = true
            delay(3.seconds)
            isHintVisible = false
        } else {
            isHintVisible = false
        }
    }

    val sendAddressBookState =
        remember(existingContact.value, recipientAddressState, isHintVisible) {
            derivedStateOf {
                val exists = existingContact.value != null
                val isValid = recipientAddressState.type?.isNotValid == false
                val mode =
                    if (isValid) {
                        if (exists) {
                            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                        } else {
                            SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK
                        }
                    } else {
                        SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK
                    }

                SendAddressBookState(
                    mode = mode,
                    isHintVisible = isHintVisible,
                    onButtonClick = {
                        when (mode) {
                            SendAddressBookState.Mode.PICK_FROM_ADDRESS_BOOK -> {
                                navController.navigate(AddressBookArgs(AddressBookArgs.PICK_CONTACT))
                            }

                            SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK -> {
                                navController.navigate(AddContactArgs(recipientAddressState.address))
                            }
                        }
                    }
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

    // Clearing form from the previous navigation destination if required
    if (sendArguments?.clearForm == true) {
        setSendStage(SendStage.Form)
        setZecSend(null)
        setRecipientAddressState(RecipientAddressState.new("", null))
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

    if (null == synchronizer || null == walletSnapshot || null == spendingKey) {
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
            onBack = onBackAction,
            onHideBalances = onHideBalances,
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
            topAppBarSubTitleState = topAppBarSubTitleState,
            walletSnapshot = walletSnapshot,
            exchangeRateState = exchangeRateState,
            sendAddressBookState = sendAddressBookState.value
        )
    }
}

private suspend fun processZip321Result(
    zip321Uri: String,
    synchronizer: Synchronizer,
    account: Account,
    setSendStage: (SendStage) -> Unit,
    setZecSend: (ZecSend?) -> Unit,
    goPaymentRequest: (ZecSend, String) -> Unit,
) {
    val request = runCatching {
        // At this point there should by only a valid Zcash address coming
        ZIP321.request(zip321Uri, null)
    }.onFailure {
        Twig.error(it) { "Failed to validate address" }
    }.getOrElse {
        false
    }
    val payment = when (request) {
        // We support only one payment currently
        is ZIP321.ParserResult.Request -> { request.paymentRequest.payments[0] }
        else -> return
    }

    val address = synchronizer
        .validateAddress(payment.recipientAddress.value)
        .toWalletAddress(payment.recipientAddress.value)

    //TODO
    val amount = Zatoshi(10)//payment.nonNegativeAmount.value.convertZecToZatoshi()

    val memo = Memo(payment.memo?.let { String(it.data, Charsets.UTF_8) } ?: "")

    val zecSend = ZecSend(
        destination = address,
        amount = amount,
        memo = memo,
        proposal = null
    )
    setZecSend(zecSend)

    runCatching {
        synchronizer.proposeFulfillingPaymentUri(account, zip321Uri)
    }.onSuccess { proposal ->
        Twig.debug { "Transaction proposal from Zip321 Uri: ${proposal.toPrettyString()}" }
        val enrichedZecSend = zecSend.copy(proposal = proposal)
        setZecSend(enrichedZecSend)
        goPaymentRequest(enrichedZecSend, zip321Uri)
    }.onFailure {
        Twig.error(it) { "Transaction proposal from Zip321 Uri failed" }
        setSendStage(SendStage.SendFailure(it.message ?: ""))
    }
}

private suspend fun AddressType.toWalletAddress(value: String) =
    when (this) {
        AddressType.Unified -> WalletAddress.Unified.new(value)
        AddressType.Shielded -> WalletAddress.Sapling.new(value)
        AddressType.Transparent -> WalletAddress.Transparent.new(value)
        else -> error("Invalid address type")
    }
