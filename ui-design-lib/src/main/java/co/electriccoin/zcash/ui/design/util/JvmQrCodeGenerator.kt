package co.electriccoin.zcash.ui.design.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

const val QR_CODE_IMAGE_MARGIN_IN_PIXELS = 2

object JvmQrCodeGenerator : QrCodeGenerator {
    override fun generate(data: String, sizePixels: Int): BooleanArray {
        val bitMatrix =
            QRCodeWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                sizePixels,
                sizePixels,
                mapOf(
                    EncodeHintType.MARGIN to QR_CODE_IMAGE_MARGIN_IN_PIXELS,
                    EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                )
            )

        return BooleanArray(sizePixels * sizePixels).apply {
            var booleanArrayPosition = 0
            for (bitMatrixX in 0 until sizePixels) {
                for (bitMatrixY in 0 until sizePixels) {
                    this[booleanArrayPosition] = bitMatrix.get(bitMatrixX, bitMatrixY)
                    booleanArrayPosition++
                }
            }
        }
    }
}
