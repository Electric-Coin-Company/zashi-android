package co.electriccoin.zcash.ui.design.util

import android.graphics.Bitmap

interface QrCodeImageGenerator {
    fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        colors: QrCodeColors
    ): Bitmap

    fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        background: Int,
        foreground: Int,
    ): Bitmap
}
