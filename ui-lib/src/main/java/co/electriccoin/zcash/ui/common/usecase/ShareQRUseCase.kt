package co.electriccoin.zcash.ui.common.usecase

import androidx.compose.ui.graphics.asAndroidBitmap
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.design.util.QrCodeColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareQRUseCase(
    private val shareImageBitmap: ShareImageUseCase,
) {
    suspend operator fun invoke(
        qrData: String,
        qrSizePx: Int,
        qrColors: QrCodeColors,
        shareText: String,
        sharePickerText: String,
        filenamePrefix: String
    ) = withContext(Dispatchers.Default) {
        val qrCodePixelArray = JvmQrCodeGenerator.generate(qrData, qrSizePx)
        val bitmap = AndroidQrCodeImageGenerator.generate(qrCodePixelArray, qrSizePx, qrColors)
        shareImageBitmap(
            shareImageBitmap = bitmap.asAndroidBitmap(),
            filePrefix = filenamePrefix,
            fileSuffix = ".png",
            shareText = shareText,
            sharePickerText = sharePickerText,
        )
    }
}