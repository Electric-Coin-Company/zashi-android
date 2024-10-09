package co.electriccoin.zcash.ui.screen.request.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getInternalCacheDirSuspend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSynchronizerUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.model.AmountState
import co.electriccoin.zcash.ui.screen.request.model.MemoState
import co.electriccoin.zcash.ui.screen.request.model.Request
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
    private val getSynchronizer: GetSynchronizerUseCase,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    enum class Stage {
        AMOUNT, MEMO, QR_CODE
    }

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
            amountState = AmountState.InValid(Zatoshi(1)),
            memoState = MemoState.InValid(""),
        )
    )

    private val stage = MutableStateFlow(Stage.AMOUNT)

    internal val state = combine(
        getAddresses(),
        request,
        stage,
        walletViewModel.exchangeRateUsd,
    ) { addresses, request, currentStage, exchangeRateUsd ->
        when (currentStage) {
            Stage.AMOUNT -> {
                RequestState.Amount(
                    request = request,
                    exchangeRateState = exchangeRateUsd,
                    zcashCurrency = getZcashCurrency(),
                    onAmount = { onAmount(it) },
                    onDone = { onDone(Stage.MEMO) },
                    onBack = ::onBack,
                )
            }
            Stage.MEMO -> {
                RequestState.Memo(
                    walletAddress = addresses
                        .fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
                    request = request,
                    onMemo = { onMemo(it) },
                    onDone = { onDone(Stage.QR_CODE) },
                    onBack = ::onBack,
                )
            }
            Stage.QR_CODE -> {
                RequestState.QrCode(
                    walletAddress = addresses
                        .fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
                    request = request,
                    onQrCodeShare = { onRequestQrCodeShare(it, versionInfo) },
                    onDone = { onDone(Stage.QR_CODE) },
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

    private fun onAmount(zatoshi: Zatoshi) = viewModelScope.launch {
        // TODO validation
        request.emit(request.value.copy(amountState = AmountState.Valid(zatoshi)))
    }

    private fun onMemo(memo: String) = viewModelScope.launch {
        // TODO validation
        request.emit(request.value.copy(memoState = MemoState.Valid(memo)))
    }

    internal fun onBack() = viewModelScope.launch {
        when (stage.value) {
            Stage.AMOUNT -> {
                backNavigationCommand.emit(Unit)
            }
            Stage.MEMO -> {
                stage.emit(Stage.AMOUNT)
            }
            Stage.QR_CODE -> {
                stage.emit(Stage.MEMO)
            }
        }
    }

    private fun onDone(newStage: Stage) = viewModelScope.launch {
        stage.emit(newStage)
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
