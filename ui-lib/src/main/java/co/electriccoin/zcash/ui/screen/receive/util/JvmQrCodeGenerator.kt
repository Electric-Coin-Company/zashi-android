package co.electriccoin.zcash.ui.screen.receive.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object JvmQrCodeGenerator : QrCodeGenerator {
    override fun generate(data: String, sizePixels: Int): BooleanArray {
        val bitMatrix = QRCodeWriter().let {
            val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q, EncodeHintType.MARGIN to 3)
            it.encode(data, BarcodeFormat.QR_CODE, sizePixels, sizePixels, hints)
        }

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
