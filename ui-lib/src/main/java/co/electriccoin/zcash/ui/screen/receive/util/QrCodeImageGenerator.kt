package co.electriccoin.zcash.ui.screen.receive.util

import androidx.compose.ui.graphics.ImageBitmap

interface QrCodeImageGenerator {
    fun generate(
        bitArray: BooleanArray,
        sizePixels: Int
    ): ImageBitmap
}
