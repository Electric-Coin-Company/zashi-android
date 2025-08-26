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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.di.koinActivityViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarVM
import co.electriccoin.zcash.ui.common.compose.LocalActivity
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.usecase.ObserveClearSendUseCase
import co.electriccoin.zcash.ui.common.usecase.PrefillSendData
import co.electriccoin.zcash.ui.common.usecase.PrefillSendUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetArgs
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetState
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetVM
import co.electriccoin.zcash.ui.screen.scan.ScanArgs
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.send.ext.Saver
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import co.electriccoin.zcash.ui.screen.send.view.Send
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
internal fun WrapSend(args: Send) {
    val activity = LocalActivity.current

    val navigationRouter = koinInject<NavigationRouter>()

    val walletViewModel = koinActivityViewModel<WalletViewModel>()

    val balanceWidgetVM =
        koinViewModel<BalanceWidgetVM> {
            parametersOf(
                BalanceWidgetArgs(
                    isBalanceButtonEnabled = true,
                    isExchangeRateButtonEnabled = false,
                    showDust = true
                )
            )
        }

    val accountDataSource = koinInject<AccountDataSource>()

    val exchangeRateRepository = koinInject<ExchangeRateRepository>()

    val hasCameraFeature = activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    val synchronizer = walletViewModel.synchronizer.collectAsStateWithLifecycle().value

    val selectedAccount = accountDataSource.selectedAccount.collectAsStateWithLifecycle(null).value

    val balanceState = balanceWidgetVM.state.collectAsStateWithLifecycle().value

    val exchangeRateState = exchangeRateRepository.state.collectAsStateWithLifecycle().value

    WrapSend(
        balanceWidgetState = balanceState,
        exchangeRateState = exchangeRateState,
        goToQrScanner = {
            navigationRouter.forward(
                ScanArgs(
                    ScanFlow.SEND,
                    isScanZip321Enabled = args.isScanZip321Enabled
                )
            )
        },
        goBack = { navigationRouter.back() },
        hasCameraFeature = hasCameraFeature,
        sendArguments = args,
        synchronizer = synchronizer,
        selectedAccount = selectedAccount
    )
}

@Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod")
@VisibleForTesting
@Composable
internal fun WrapSend(
    balanceWidgetState: BalanceWidgetState,
    exchangeRateState: ExchangeRateState,
    goToQrScanner: () -> Unit,
    goBack: () -> Unit,
    hasCameraFeature: Boolean,
    sendArguments: Send,
    synchronizer: Synchronizer?,
    selectedAccount: WalletAccount?,
) {
    val scope = rememberCoroutineScope()

    val viewModel = koinViewModel<SendViewModel>()

    val sendAddressBookState by viewModel.sendAddressBookState.collectAsStateWithLifecycle()

    val topAppBarViewModel = koinActivityViewModel<ZashiTopAppBarVM>()

    val zashiMainTopAppBarState by topAppBarViewModel.state.collectAsStateWithLifecycle()

    val locale = LocalConfiguration.current.locales[0]

    val (sendStage, setSendStage) =
        rememberSaveable(stateSaver = SendStage.Saver) { mutableStateOf(SendStage.Form) }

    val (zecSend, setZecSend) = rememberSaveable(stateSaver = ZecSend.Saver) { mutableStateOf(null) }

    val recipientAddressState by viewModel.recipientAddressState.collectAsStateWithLifecycle()

    val observeClearSend = koinInject<ObserveClearSendUseCase>()
    val prefillSend = koinInject<PrefillSendUseCase>()

    if (sendArguments.recipientAddress != null && sendArguments.recipientAddressType != null) {
        viewModel.onRecipientAddressChanged(
            RecipientAddressState.new(
                sendArguments.recipientAddress,
                when (sendArguments.recipientAddressType) {
                    cash.z.ecc.sdk.model.AddressType.UNIFIED -> AddressType.Unified
                    cash.z.ecc.sdk.model.AddressType.TRANSPARENT -> AddressType.Transparent
                    cash.z.ecc.sdk.model.AddressType.SAPLING -> AddressType.Shielded
                    cash.z.ecc.sdk.model.AddressType.TEX -> AddressType.Tex
                }
            )
        )
    }

    // Amount computation:
    val (amountState, setAmountState) =
        rememberSaveable(locale, stateSaver = AmountState.Saver) {
            // Default amount state
            mutableStateOf(
                AmountState.newFromZec(
                    value = zecSend?.amount?.toZecString() ?: "",
                    fiatValue = "",
                    isTransparentOrTextRecipient =
                        recipientAddressState.type?.let { it == AddressType.Transparent }
                            ?: false,
                    exchangeRateState = exchangeRateState,
                    locale = locale,
                )
            )
        }
    // New amount state based on the recipient address type (e.g. shielded supports zero funds sending and
    // transparent not)
    LaunchedEffect(recipientAddressState, exchangeRateState) {
        setAmountState(
            if (amountState.value.isNotBlank() || amountState.fiatValue.isBlank()) {
                AmountState.newFromZec(
                    value = amountState.value,
                    fiatValue = amountState.fiatValue,
                    isTransparentOrTextRecipient =
                        recipientAddressState.type
                            ?.let { it == AddressType.Transparent } ?: false,
                    exchangeRateState = exchangeRateState,
                    lastFieldChangedByUser = amountState.lastFieldChangedByUser,
                    locale = locale,
                )
            } else {
                AmountState.newFromFiat(
                    value = amountState.value,
                    fiatValue = amountState.fiatValue,
                    isTransparentOrTextRecipient =
                        recipientAddressState.type
                            ?.let { it == AddressType.Transparent } ?: false,
                    exchangeRateState = exchangeRateState,
                    locale = locale,
                )
            }
        )
    }

    // Memo computation:
    val (memoState, setMemoState) =
        rememberSaveable(stateSaver = MemoState.Saver) {
            mutableStateOf(MemoState.new(zecSend?.memo?.value ?: ""))
        }

    LaunchedEffect(locale) {
        observeClearSend().collect {
            setSendStage(SendStage.Form)
            setZecSend(null)
            viewModel.onRecipientAddressChanged(RecipientAddressState.new("", null))
            setAmountState(
                AmountState.newFromZec(
                    value = "",
                    fiatValue = "",
                    isTransparentOrTextRecipient = false,
                    exchangeRateState = exchangeRateState,
                    locale = locale
                )
            )
            setMemoState(MemoState.new(""))
        }
    }

    LaunchedEffect(locale) {
        prefillSend().collect {
            when (it) {
                is PrefillSendData.All -> {
                    val type = synchronizer?.validateAddress(it.address.orEmpty())
                    setSendStage(SendStage.Form)
                    setZecSend(null)
                    viewModel.onRecipientAddressChanged(
                        RecipientAddressState.new(
                            address = it.address.orEmpty(),
                            type = type
                        )
                    )

                    val fee = it.fee
                    val value = if (fee == null) it.amount else it.amount - fee

                    setAmountState(
                        AmountState.newFromZec(
                            value = value.convertZatoshiToZecString(),
                            fiatValue = amountState.fiatValue,
                            isTransparentOrTextRecipient = type == AddressType.Transparent,
                            exchangeRateState = exchangeRateState,
                            locale = locale
                        )
                    )
                    setMemoState(MemoState.new(it.memos?.firstOrNull().orEmpty()))
                }

                is PrefillSendData.FromAddressScan -> {
                    val type = synchronizer?.validateAddress(it.address)
                    setSendStage(SendStage.Form)
                    setZecSend(null)
                    viewModel.onRecipientAddressChanged(
                        RecipientAddressState.new(
                            address = it.address,
                            type = type
                        )
                    )
                }
            }
        }
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

    if (null == synchronizer || null == selectedAccount) {
        // TODO [#1146]: Consider moving CircularScreenProgressIndicator from Android layer to View layer
        // TODO [#1146]: Improve this by allowing screen composition and updating it after the data is available
        // TODO [#1146]: https://github.com/Electric-Coin-Company/zashi-android/issues/1146
        CircularScreenProgressIndicator()
    } else {
        Send(
            balanceWidgetState = balanceWidgetState,
            sendStage = sendStage,
            onCreateZecSend = { newZecSend ->
                viewModel.onCreateZecSendClick(
                    newZecSend = newZecSend,
                    amountState = amountState,
                    setSendStage = setSendStage
                )
            },
            onBack = onBackAction,
            onQrScannerOpen = goToQrScanner,
            hasCameraFeature = hasCameraFeature,
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
            setAmountState = setAmountState,
            amountState = amountState,
            setMemoState = setMemoState,
            memoState = memoState,
            selectedAccount = selectedAccount,
            exchangeRateState = exchangeRateState,
            sendAddressBookState = sendAddressBookState,
            zashiMainTopAppBarState = zashiMainTopAppBarState
        )
    }
}
