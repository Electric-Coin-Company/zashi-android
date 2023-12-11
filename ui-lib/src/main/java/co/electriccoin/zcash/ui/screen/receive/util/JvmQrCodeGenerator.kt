package co.electriccoin.zcash.ui.screen.receive.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object JvmQrCodeGenerator : QrCodeGenerator {
    override fun generate(
        data: String,
        sizePixels: Int
    ): BooleanArray {
        val bitMatrix =
            QRCodeWriter().let {
                it.encode(data, BarcodeFormat.QR_CODE, sizePixels, sizePixels)
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
