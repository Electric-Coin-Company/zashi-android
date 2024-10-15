package co.electriccoin.zcash.ui.screen.request.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareQrImageUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321BuildUriUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.qrcode.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.qrcode.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.ext.convertToDouble
import co.electriccoin.zcash.ui.screen.request.model.AmountState
import co.electriccoin.zcash.ui.screen.request.model.MemoState
import co.electriccoin.zcash.ui.screen.request.model.OnAmount
import co.electriccoin.zcash.ui.screen.request.model.QrCodeState
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestCurrency
import co.electriccoin.zcash.ui.screen.request.model.RequestStage
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RequestViewModel(
    private val addressTypeOrdinal: Int,
    private val application: Application,
    getAddresses: GetAddressesUseCase,
    walletViewModel: WalletViewModel,
    getZcashCurrency: GetZcashCurrencyProvider,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    shareImageBitmap: ShareQrImageUseCase,
    zip321BuildUriUseCase: Zip321BuildUriUseCase,
) : ViewModel() {
    private val DEFAULT_AMOUNT = application.getString(R.string.request_amount_empty)
    private val DEFAULT_MEMO = ""
    private val DEFAULT_QR_CODE_URI = ""

    internal val request = MutableStateFlow(
        Request(
            amountState = AmountState.Default(DEFAULT_AMOUNT, RequestCurrency.Zec),
            memoState = MemoState.Valid(DEFAULT_MEMO, 0, DEFAULT_AMOUNT),
            qrCodeState = QrCodeState(DEFAULT_QR_CODE_URI, DEFAULT_AMOUNT, DEFAULT_MEMO, null),
        )
    )

    private val stage = MutableStateFlow(RequestStage.AMOUNT)

    internal val state = combine(
        getAddresses(),
        request,
        stage,
        walletViewModel.exchangeRateUsd,
    ) { addresses, request, currentStage, exchangeRateUsd ->
        val walletAddress = addresses.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal))

        when (currentStage) {
            RequestStage.AMOUNT -> {
                RequestState.Amount(
                    exchangeRateState = exchangeRateUsd,
                    monetarySeparators = getMonetarySeparators(),
                    onAmount = { onAmount(resolveExchangeRateValue(exchangeRateUsd), it) },
                    onBack = ::onBack,
                    onDone = { onAmountDone(resolveExchangeRateValue(exchangeRateUsd)) },
                    onSwitch = { onSwitch(resolveExchangeRateValue(exchangeRateUsd), it) },
                    request = request,
                    zcashCurrency = getZcashCurrency(),
                )
            }
            RequestStage.MEMO -> {
                RequestState.Memo(
                    walletAddress = walletAddress,
                    request = request,
                    onMemo = { onMemo(it) },
                    onDone = { onMemoDone(walletAddress.address, zip321BuildUriUseCase) },
                    onBack = ::onBack,
                    zcashCurrency = getZcashCurrency(),
                )
            }
            RequestStage.QR_CODE -> {
                RequestState.QrCode(
                    walletAddress = walletAddress,
                    request = request,
                    onQrCodeGenerate = { qrCodeForValue(request.qrCodeState.requestUri, it) },
                    onQrCodeShare = { onRequestQrCodeShare(it, shareImageBitmap) },
                    onBack = ::onBack,
                    onClose = ::onClose,
                    zcashCurrency = getZcashCurrency(),
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
        initialValue = RequestState.Loading
    )

    val backNavigationCommand = MutableSharedFlow<Unit>()

    val shareResultCommand = MutableSharedFlow<Boolean>()

    private fun resolveExchangeRateValue(exchangeRateUsd: ExchangeRateState): FiatCurrencyConversion? {
        return when (exchangeRateUsd) {
            is ExchangeRateState.Data -> {
                if (exchangeRateUsd.currencyConversion == null) {
                    Twig.warn { "Currency conversion is currently not available" }
                    null
                } else {
                    exchangeRateUsd.currencyConversion
                }
            }
            else -> {
                // Should not happen as the conversion rate related use cases should not be available
                Twig.error { "Unexpected screen state" }
                null
            }
        }
    }

    private fun onAmount(conversion: FiatCurrencyConversion?, onAmount: OnAmount) = viewModelScope.launch {
        val newState = when(onAmount) {
            is OnAmount.Number -> {
                if (request.value.amountState.amount == DEFAULT_AMOUNT) {
                    // Special case with current value only zero
                    validateAmountState(conversion, onAmount.number.toString())
                } else {
                    // Adding new number to the result string
                    validateAmountState(
                        conversion,
                        request.value.amountState.amount + onAmount.number
                    )
                }
            }
            is OnAmount.Delete -> {
                if (request.value.amountState.amount.length == 1) {
                    // Deleting up to the last character
                    AmountState.Default(DEFAULT_AMOUNT, request.value.amountState.currency)
                } else {
                    validateAmountState(conversion, request.value.amountState.amount.dropLast(1))
                }
            }
            is OnAmount.Separator -> {
                if (request.value.amountState.amount.contains(onAmount.separator)) {
                    // Separator already present
                    validateAmountState(conversion, request.value.amountState.amount)
                } else {
                    validateAmountState(
                        conversion,
                        request.value.amountState.amount + onAmount.separator
                    )
                }
            }
        }
        request.emit(
            request.value.copy(amountState = newState)
        )
    }

    // Validates only zeros and decimal separator
    private val defaultAmountValidationRegex = "^[${DEFAULT_AMOUNT}${getMonetarySeparators().decimal}]*$".toRegex()
    // Validates only numbers the properly use grouping and decimal separators
    // Note that this regex aligns with the one from ZcashSDK (sdk-incubator-lib/src/main/res/values/strings-regex.xml)
    // It only adds check for 0-8 digits after the decimal separator at maximum
    private val allowedNumberFormatValidationRegex = "^([0-9]*([0-9]+([${getMonetarySeparators().grouping}]\$|[${getMonetarySeparators().grouping}][0-9]+))*([${getMonetarySeparators().decimal}]\$|[${getMonetarySeparators().decimal}][0-9]{0,8})?)?\$".toRegex()

    private fun validateAmountState(
        conversion: FiatCurrencyConversion?,
        resultAmount: String,
    ): AmountState {
        val newAmount = if (resultAmount.contains(defaultAmountValidationRegex)) {
            AmountState.Default(
                // Check for the max decimals in the default (i.e. 0.000) number, too
                if (!resultAmount.contains(allowedNumberFormatValidationRegex)) {
                    request.value.amountState.amount
                } else {
                    resultAmount
                },
                request.value.amountState.currency
            )
        } else if (!resultAmount.contains(allowedNumberFormatValidationRegex)) {
            AmountState.Valid(request.value.amountState.amount, request.value.amountState.currency)
        } else {
            AmountState.Valid(resultAmount, request.value.amountState.currency)
        }

        // Check for max Zcash supply
        return newAmount.amount.convertToDouble()?.let { currentValue ->
            val zecValue = if (newAmount.currency == RequestCurrency.Fiat && conversion != null) {
                currentValue / conversion.priceOfZec
            } else {
                currentValue
            }
            if (zecValue > 21_000_000) {
                newAmount.copyState(request.value.amountState.amount)
            } else {
                newAmount
            }
        } ?: newAmount
    }

    internal fun onBack() = viewModelScope.launch {
        when (stage.value) {
            RequestStage.AMOUNT -> {
                backNavigationCommand.emit(Unit)
            }
            RequestStage.MEMO -> {
                stage.emit(RequestStage.AMOUNT)
            }
            RequestStage.QR_CODE -> {
                stage.emit(RequestStage.MEMO)
            }
        }
    }

    private fun onClose() = viewModelScope.launch {
        backNavigationCommand.emit(Unit)
    }

    private fun onAmountDone(conversion: FiatCurrencyConversion?) = viewModelScope.launch {
        val memoAmount = when (request.value.amountState.currency) {
            RequestCurrency.Fiat -> if (conversion != null) {
                request.value.amountState.toZecString(conversion)
            } else {
                Twig.error { "Unexpected screen state" }
                request.value.amountState.amount
            }

            RequestCurrency.Zec -> request.value.amountState.amount
        }
        request.emit(request.value.copy(memoState = MemoState.new(DEFAULT_MEMO, memoAmount)))
        stage.emit(RequestStage.MEMO)
    }

    private fun onMemoDone(address: String, zip321BuildUriUseCase: Zip321BuildUriUseCase) = viewModelScope.launch {
        request.emit(request.value.copy(
            qrCodeState = QrCodeState(
                requestUri = createZip321Uri(
                    address = address,
                    amount = request.value.memoState.zecAmount,
                    memo = request.value.memoState.text,
                    zip321BuildUriUseCase = zip321BuildUriUseCase
                ),
                zecAmount = request.value.memoState.zecAmount,
                memo = request.value.memoState.text,
                bitmap = null
            ))
        )
        stage.emit(RequestStage.QR_CODE)
    }

    private fun onSwitch(conversion: FiatCurrencyConversion?, onSwitchTo: RequestCurrency) = viewModelScope.launch {
        if (conversion == null) {
            return@launch
        }
        val newAmount = when(onSwitchTo) {
            is RequestCurrency.Fiat -> {
                request.value.amountState.toFiatString(
                    application.applicationContext,
                    conversion
                )
            }
            is RequestCurrency.Zec -> {
                request.value.amountState.toZecString(
                    conversion
                )
            }
        }

        // Check default value and shrink it to 0 if necessary
        val newState = if (newAmount.contains(defaultAmountValidationRegex)) {
            request.value.amountState.copyState(DEFAULT_AMOUNT, onSwitchTo)
        } else {
            request.value.amountState.copyState(newAmount, onSwitchTo)
        }

        request.emit(
            request.value.copy(amountState = newState)
        )
    }

    private fun onMemo(memoState: MemoState) = viewModelScope.launch {
        request.emit(request.value.copy(memoState = memoState))
    }

    private fun createZip321Uri(
        address: String,
        amount: String,
        memo: String,
        zip321BuildUriUseCase: Zip321BuildUriUseCase,
    ): String {
        return zip321BuildUriUseCase.invoke(
            address = address,
            amount = amount,
            memo = memo
        )
    }

    private fun onRequestQrCodeShare(
        bitmap: ImageBitmap,
        shareImageBitmap: ShareQrImageUseCase,
    ) = viewModelScope.launch {
        shareImageBitmap(
            shareImageBitmap = bitmap.asAndroidBitmap(),
            filePrefix = TEMP_FILE_NAME_PREFIX,
            fileSuffix = TEMP_FILE_NAME_SUFFIX,
            shareText = application.getString(R.string.request_qr_code_share_chooser_text),
            sharePickerText = application.getString(R.string.request_qr_code_share_chooser_title),
        ).collect { shareResult ->
            if (shareResult) {
                Twig.info { "Sharing the request QR code was successful" }
                shareResultCommand.emit(true)
            } else {
                Twig.info { "Sharing the request QR code failed" }
                shareResultCommand.emit(false)
            }
        }
    }

    private fun qrCodeForValue(
        value: String,
        size: Int,
    ) = viewModelScope.launch {
        // In the future, use actual/expect to switch QR code generator implementations for multiplatform

        // Note that our implementation has an extra array copy to BooleanArray, which is a cross-platform
        // representation.  This should have minimal performance impact since the QR code is relatively
        // small and we only generate QR codes infrequently.

        val qrCodePixelArray = JvmQrCodeGenerator.generate(value, size)
        val bitmap = AndroidQrCodeImageGenerator.generate(qrCodePixelArray, size)

        val newQrCodeState = request.value.qrCodeState.copy(bitmap = bitmap)
        request.emit(request.value.copy(qrCodeState = newQrCodeState))
    }
}

private const val TEMP_FILE_NAME_PREFIX = "zip_321_request_qr_" // NON-NLS
private const val TEMP_FILE_NAME_SUFFIX = ".png" // NON-NLS
