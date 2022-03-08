package co.electriccoin.zcash.ui.screen.profile.util

import androidx.compose.ui.graphics.ImageBitmap

interface QrCodeImageGenerator {
    fun generate(bitArray: BooleanArray, sizePixels: Int): ImageBitmap
}
