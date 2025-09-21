package co.electriccoin.zcash.ui.common.usecase

import androidx.compose.ui.graphics.asAndroidBitmap
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareQRUseCase(
    private val shareImageBitmap: ShareImageUseCase,
) {
    suspend operator fun invoke(
        qrData: String,
        shareText: String,
        sharePickerText: String,
        filenamePrefix: String
    ) = withContext(Dispatchers.Default) {
        val qrCodePixelArray = JvmQrCodeGenerator.generate(
            data = qrData,
            sizePixels = QR_SIZE_PX
        )
        val bitmap = AndroidQrCodeImageGenerator.generate(
            bitArray = qrCodePixelArray,
            sizePixels = QR_SIZE_PX,
            background = WHITE,
            foreground = BLACK,
        )
        shareImageBitmap(
            shareImageBitmap = bitmap.asAndroidBitmap(),
            filePrefix = filenamePrefix,
            fileSuffix = ".png",
            shareText = shareText,
            sharePickerText = sharePickerText,
        )
    }
}

private const val QR_SIZE_PX = 1280
private const val WHITE = 0xFFFFFFFF.toInt()
private const val BLACK = 0xFF000000.toInt()
