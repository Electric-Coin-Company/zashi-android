package cash.z.ecc.ui.screen.profile.util

import androidx.compose.ui.graphics.ImageBitmap

interface QrCodeImageGenerator {
    fun generate(bitArray: BooleanArray, sizePixels: Int): ImageBitmap
}
