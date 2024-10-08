package co.electriccoin.zcash.ui.screen.request.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getInternalCacheDirSuspend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import co.electriccoin.zcash.ui.util.FileShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RequestViewModel(
    private val addressTypeOrdinal: Int,
    private val application: Application,
    getAddresses: GetAddressesUseCase,
    getVersionInfo: GetVersionInfoProvider,
) : ViewModel() {
    private val versionInfo by lazy { getVersionInfo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val state =
        getAddresses().mapLatest { addresses ->
            RequestState.Prepared(
                walletAddress = addresses.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal)),
                onRequestQrCodeShare = { /*onRequestQrCodeShareClick(, versionInfo)*/ },
                onBack = ::onBack,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = RequestState.Loading
        )

    val backNavigationCommand = MutableSharedFlow<Unit>()

    val shareResultCommand = MutableSharedFlow<Boolean>()

    private fun onBack() =
        viewModelScope.launch {
            backNavigationCommand.emit(Unit)
        }

    private fun onRequestQrCodeShareClick(
        //bitmap: ImageBitmap,
        versionInfo: VersionInfo
    ) = viewModelScope.launch {

        



        // shareData(
        //     context = application.applicationContext,
        //     qrImageBitmap = bitmap.asAndroidBitmap(),
        //     versionInfo = versionInfo
        // ).collect { shareResult ->
        //     if (shareResult) {
        //         Twig.info { "Sharing the request QR code was successful" }
        //         shareResultCommand.emit(true)
        //     } else {
        //         Twig.info { "Sharing the request QR code failed" }
        //         shareResultCommand.emit(false)
        //     }
        // }
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
