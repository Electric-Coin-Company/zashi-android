@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.receive

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.spackle.ClipboardManagerUtil
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.spackle.getInternalCacheDirSuspend
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.receive.view.Receive
import co.electriccoin.zcash.ui.util.FileShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
internal fun WrapReceive(
    activity: ComponentActivity,
    onSettings: () -> Unit,
) {
    val viewModel by activity.viewModels<WalletViewModel>()
    val walletAddresses = viewModel.addresses.collectAsStateWithLifecycle().value

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val versionInfo = VersionInfo.new(activity.applicationContext)

    Receive(
        walletAddress = walletAddresses,
        snackbarHostState = snackbarHostState,
        onAdjustBrightness = { /* Just for testing purposes */ },
        onAddrCopyToClipboard = { address ->
            ClipboardManagerUtil.copyToClipboard(
                activity.applicationContext,
                activity.getString(R.string.receive_clipboard_tag),
                address
            )
        },
        onQrImageShare = { imageBitmap ->
            scope.launch {
                shareData(
                    context = activity.applicationContext,
                    snackbarHostState = snackbarHostState,
                    qrImageBitmap = imageBitmap.asAndroidBitmap(),
                    versionInfo = versionInfo
                ).collect { shareResult ->
                    Twig.info {
                        if (shareResult) {
                            "Sharing the address QR code was successful"
                        } else {
                            "Sharing the address QR code failed"
                        }
                    }
                    // No other action for now
                }
            }
        },
        onSettings = onSettings,
        versionInfo = versionInfo
    )
}

private const val CACHE_SUBDIR = "zcash_address_qr_images" // NON-NLS
private const val TEMP_FILE_NAME_PREFIX = "zcash_address_qr_" // NON-NLS
private const val TEMP_FILE_NAME_SUFFIX = ".png" // NON-NLS

fun shareData(
    context: Context,
    snackbarHostState: SnackbarHostState,
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
                versionInfo = versionInfo,
                fileType = FileShareUtil.ZASHI_QR_CODE_MIME_TYPE
            )
        runCatching {
            context.startActivity(shareIntent)
            trySend(true)
        }.onFailure {
            snackbarHostState.showSnackbar(message = context.getString(R.string.receive_data_unable_to_share))
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
