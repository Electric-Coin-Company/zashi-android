package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareQRUseCase(
    private val shareImageBitmap: ShareImageUseCase,
    private val context: Context
) {
    @Suppress("MagicNumber")
    suspend operator fun invoke(
        qrData: String,
        shareText: String,
        sharePickerText: String,
        filenamePrefix: String,
        @DrawableRes centerIcon: Int? = null
    ) = withContext(Dispatchers.Default) {
        val qrCodePixelArray = JvmQrCodeGenerator.generate(data = qrData, sizePixels = QR_SIZE_PX)
        val bitmap =
            AndroidQrCodeImageGenerator
                .generate(
                    bitArray = qrCodePixelArray,
                    sizePixels = QR_SIZE_PX,
                    background = WHITE,
                    foreground = BLACK,
                ).copy(Bitmap.Config.ARGB_8888, true)

        if (centerIcon != null) {
            val drawable = ContextCompat.getDrawable(context, centerIcon)
            if (drawable != null) {
                val canvas = Canvas(bitmap)
                val left = bitmap.width / 2 - (bitmap.width * .09f).toInt()
                val right = bitmap.width / 2 + (bitmap.width * .09f).toInt()
                val top = bitmap.height / 2 - (bitmap.height * .09f).toInt()
                val bottom = bitmap.height / 2 + (bitmap.height * .09f).toInt()
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }
        }

        shareImageBitmap(
            shareImageBitmap = bitmap,
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
