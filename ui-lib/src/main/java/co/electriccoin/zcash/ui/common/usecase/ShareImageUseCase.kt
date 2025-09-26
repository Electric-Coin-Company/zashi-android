package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.graphics.Bitmap
import co.electriccoin.zcash.spackle.getInternalCacheDirSuspend
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.common.provider.GetVersionInfoProvider
import co.electriccoin.zcash.ui.util.FileShareUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val CACHE_SUBDIR = "zashi_qr_images" // NON-NLS

class ShareImageUseCase(
    private val context: Context,
    private val versionInfoProvider: GetVersionInfoProvider
) {
    suspend operator fun invoke(
        shareImageBitmap: Bitmap,
        shareText: String? = null,
        sharePickerText: String,
        filePrefix: String = "",
        fileSuffix: String = ""
    ) = shareData(
        context = context,
        shareImageBitmap = shareImageBitmap,
        versionInfo = versionInfoProvider(),
        filePrefix = filePrefix,
        fileSuffix = fileSuffix,
        shareText = shareText,
        sharePickerText = sharePickerText,
    )

    private suspend fun shareData(
        context: Context,
        shareImageBitmap: Bitmap,
        shareText: String?,
        sharePickerText: String,
        versionInfo: VersionInfo,
        filePrefix: String,
        fileSuffix: String,
    ): Boolean {
        // Initialize cache directory
        val cacheDir = context.getInternalCacheDirSuspend(CACHE_SUBDIR)

        // Save the bitmap to a temporary file in the cache directory
        val bitmapFile =
            withContext(Dispatchers.IO) {
                File
                    .createTempFile(
                        filePrefix,
                        fileSuffix,
                        cacheDir,
                    ).also {
                        it.storeBitmap(shareImageBitmap)
                    }
            }

        // Example of the expected temporary file path:
        // /data/user/0/co.electriccoin.zcash.debug/cache/zashi_qr_images/
        // zcash_address_qr_6455164324646067652.png

        val shareIntent =
            FileShareUtil.newShareContentIntent(
                context = context,
                dataFilePath = bitmapFile.absolutePath,
                fileType = FileShareUtil.ZASHI_QR_CODE_MIME_TYPE,
                shareText = shareText,
                sharePickerText = sharePickerText,
                versionInfo = versionInfo,
            )
        return runCatching {
            context.startActivity(shareIntent)
            true
        }.getOrElse { false }
    }

    private suspend fun File.storeBitmap(bitmap: Bitmap) =
        withContext(Dispatchers.IO) {
            outputStream().use { fOut ->
                @Suppress("MagicNumber")
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
            }
        }
}
