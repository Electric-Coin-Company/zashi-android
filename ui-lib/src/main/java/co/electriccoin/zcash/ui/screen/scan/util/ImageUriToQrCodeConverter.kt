package co.electriccoin.zcash.ui.screen.scan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import co.electriccoin.zcash.spackle.Twig
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageUriToQrCodeConverter {
    suspend operator fun invoke(
        context: Context,
        uri: Uri
    ): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                uri
                    .toBitmap(context)
                    .toBinaryBitmap()
                    .toQRCode()
            }.onFailure {
                Twig.error(it) { "Failed to convert Uri to QR code" }
            }.getOrNull()
        }

    private fun Uri.toBitmap(context: Context): Bitmap =
        context.contentResolver
            .openInputStream(this)
            .use {
                BitmapFactory.decodeStream(it)
            }

    private fun Bitmap.toBinaryBitmap(): BinaryBitmap {
        val width = this.width
        val height = this.height
        val pixels = IntArray(width * height)
        this.getPixels(pixels, 0, width, 0, 0, width, height)
        this.recycle()
        val source = RGBLuminanceSource(width, height, pixels)
        return BinaryBitmap(HybridBinarizer(source))
    }

    private fun BinaryBitmap.toQRCode(): String =
        MultiFormatReader()
            .apply {
                setHints(
                    mapOf(
                        DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
                        DecodeHintType.ALSO_INVERTED to true
                    )
                )
            }.decodeWithState(this@toQRCode)
            .text
}
