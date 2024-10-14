package co.electriccoin.zcash.ui.screen.request.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getInternalCacheDirSuspend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.ext.convertToDouble
import co.electriccoin.zcash.ui.screen.request.model.AmountState
import co.electriccoin.zcash.ui.screen.request.model.MemoState
import co.electriccoin.zcash.ui.screen.request.model.OnAmount
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestCurrency
import co.electriccoin.zcash.ui.screen.request.model.RequestStage
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import co.electriccoin.zcash.ui.util.FileShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RequestViewModel(
    private val addressTypeOrdinal: Int,
    private val application: Application,
    getAddresses: GetAddressesUseCase,
    getVersionInfo: GetVersionInfoProvider,
    walletViewModel: WalletViewModel,
    getZcashCurrency: GetZcashCurrencyProvider,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    private val getSynchronizer: GetSynchronizerUseCase,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    private val DEFAULT_AMOUNT = application.getString(R.string.request_amount_empty)
    private val DEFAULT_MEMO = ""

    // Request(
    // amount = Zatoshi(1),
    // memo = "Test memo",
    // recipientAddress =
    // runBlocking {
    //     WalletAddress.Unified.new("u1kpy0mhprcx64400thhj9xfp862j2dhrnl7nx37c8y8pn8l58n7t2pj3vy58zg37lr4zkfwp8h868ra8wjvmrpeuqff8r6h3lzdyvdv7ly04dwkxu88mu7ze49xx7we08suux6350m2z9eljtt5a75dscc56vckhn9u0uwvdry00mehs82wjfml4fmd28e64n5ruqltyn0e6nqr726vt")
    // }
    // )

    // walletAddress = addresses.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),

    internal val request = MutableStateFlow(
        Request(
            amountState = AmountState.Default(DEFAULT_AMOUNT, RequestCurrency.Zec),
            memoState = MemoState.Valid(DEFAULT_MEMO, 0, ""),
        )
    )

    private val stage = MutableStateFlow(RequestStage.AMOUNT)

    internal val state = combine(
        getAddresses(),
        request,
        stage,
        walletViewModel.exchangeRateUsd,
    ) { addresses, request, currentStage, exchangeRateUsd ->
        when (currentStage) {
            RequestStage.AMOUNT -> {
                RequestState.Amount(
                    exchangeRateState = exchangeRateUsd,
                    monetarySeparators = getMonetarySeparators(),
                    onAmount = { onAmount(resolveExchangeRateValue(exchangeRateUsd), it) },
                    onBack = ::onBack,
                    onDone = { onDone(RequestStage.AMOUNT, resolveExchangeRateValue(exchangeRateUsd)) },
                    onSwitch = { onSwitch(resolveExchangeRateValue(exchangeRateUsd), it) },
                    request = request,
                    zcashCurrency = getZcashCurrency(),
                )
            }
            RequestStage.MEMO -> {
                RequestState.Memo(
                    walletAddress = addresses
                        .fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
                    request = request,
                    onMemo = { onMemo(it) },
                    onDone = { onDone(RequestStage.MEMO, null) },
                    onBack = ::onBack,
                    zcashCurrency = getZcashCurrency(),
                )
            }
            RequestStage.QR_CODE -> {
                RequestState.QrCode(
                    walletAddress = addresses
                        .fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
                    request = request,
                    onQrCodeShare = { onRequestQrCodeShare(it, versionInfo) },
                    onDone = { onDone(
                        currentStage = RequestStage.QR_CODE,
                        conversion = null,
                    ) },
                    onBack = ::onBack,
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

    private fun onDone(currentStage: RequestStage, conversion: FiatCurrencyConversion?) = viewModelScope.launch {
        val newStage = when(currentStage) {
            RequestStage.AMOUNT -> {
                val memoAmount = when(request.value.amountState.currency) {
                    RequestCurrency.Fiat -> if (conversion != null) {
                        request.value.amountState.toZecString(conversion)
                    } else {
                        Twig.error { "Unexpected screen state" }
                        request.value.amountState.amount
                    }
                    RequestCurrency.Zec -> request.value.amountState.amount
                }
                request.emit(request.value.copy(memoState = MemoState.new(DEFAULT_MEMO, memoAmount)))
                RequestStage.MEMO
            }
            RequestStage.MEMO -> {
                RequestStage.QR_CODE
            }
            RequestStage.QR_CODE -> TODO()
        }
        stage.emit(newStage)
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

    // private fun onRequest(request: Request) = viewModelScope.launch {
    //     val payment = Payment(
    //         recipientAddress = RecipientAddress(request.recipientAddress.address),
    //         nonNegativeAmount = NonNegativeAmount(request.amount.toZecString()),
    //         memo = MemoBytes(request.memo),
    //         label = "Test label",
    //         message = "Thank you for your purchase",
    //         otherParams = null
    //     )
    //
    //     val paymentRequest = PaymentRequest(payments = listOf(payment))
    //
    //     val zip321Uri = ZIP321.uriString(
    //         paymentRequest,
    //         ZIP321.FormattingOptions.UseEmptyParamIndex(omitAddressLabel = true)
    //     )
    //
    //     val zip321Request = ZIP321.request(
    //         payment,
    //         ZIP321.FormattingOptions.UseEmptyParamIndex(omitAddressLabel = true)
    //     )
    //
    //     Twig.error { "ZIP321: Request: $zip321Request" }
    //     Twig.error { "ZIP321: URI: $zip321Uri" }
    //
    //     val proposal = getSynchronizer().proposeFulfillingPaymentUri(Account.DEFAULT, zip321Uri)
    //
    //     Twig.error { "ZIP321: Proposal: ${proposal.toPrettyString()}" }
    //
    //     val paymentRequestFromUri = ZIP321.request(zip321Uri, null)
    //
    //     Twig.error { "ZIP321: Proposal from Uri: $paymentRequestFromUri" }
    // }

    private fun onRequestQrCodeShare(
        bitmap: ImageBitmap,
        versionInfo: VersionInfo
    ) = viewModelScope.launch {
        shareData(
            context = application.applicationContext,
            qrImageBitmap = bitmap.asAndroidBitmap(),
            versionInfo = versionInfo
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
}

private const val CACHE_SUBDIR = "zcash_address_qr_images" // NON-NLS
private const val TEMP_FILE_NAME_PREFIX = "zcash_request_qr_" // NON-NLS
private const val TEMP_FILE_NAME_SUFFIX = ".png" // NON-NLS

fun shareData(
    context: Context,
    qrImageBitmap: Bitmap,
    versionInfo: VersionInfo
): Flow<Boolean> =
    callbackFlow {
        // Initialize cache directory
        val cacheDir = context.getInternalCacheDirSuspend(CACHE_SUBDIR)

        // Save the bitmap to a temporary file in the cache directory
        val bitmapFile =
            withContext(Dispatchers.IO) {
                File.createTempFile(
                    TEMP_FILE_NAME_PREFIX,
                    TEMP_FILE_NAME_SUFFIX,
                    cacheDir,
                ).also {
                    it.storeBitmap(qrImageBitmap)
                }
            }

        // Example of the expected temporary file path:
        // /data/user/0/co.electriccoin.zcash.debug/cache/zcash_address_qr_images/
        // zcash_address_qr_6455164324646067652.png

        val shareIntent =
            FileShareUtil.newShareContentIntent(
                context = context,
                dataFilePath = bitmapFile.absolutePath,
                fileType = FileShareUtil.ZASHI_QR_CODE_MIME_TYPE,
                shareText = context.getString(R.string.request_share_chooser_text),
                sharePickerText = context.getString(R.string.request_share_chooser_title),
                versionInfo = versionInfo,
            )
        runCatching {
            context.startActivity(shareIntent)
            trySend(true)
        }.onFailure {
            trySend(false)
        }
        awaitClose {
            // No resources to release
        }
    }

suspend fun File.storeBitmap(bitmap: Bitmap) =
    withContext(Dispatchers.IO) {
        outputStream().use { fOut ->
            @Suppress("MagicNumber")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
        }
    }
