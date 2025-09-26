package co.electriccoin.zcash.ui.design.util

import androidx.compose.ui.graphics.ImageBitmap

interface QrCodeImageGenerator {
    fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        colors: QrCodeColors
    ): ImageBitmap

    fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        background: Int,
        foreground: Int,
    ): ImageBitmap
}
