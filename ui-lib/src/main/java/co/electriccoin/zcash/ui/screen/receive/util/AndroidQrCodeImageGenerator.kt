package co.electriccoin.zcash.ui.screen.receive.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object AndroidQrCodeImageGenerator : QrCodeImageGenerator {
    override fun generate(
        bitArray: BooleanArray,
        sizePixels: Int
    ): ImageBitmap {
        val colorArray = bitArray.toBlackAndWhiteColorArray()

        return Bitmap.createBitmap(colorArray, sizePixels, sizePixels, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}

// TODO [#1473]: Dark mode QR codes for Receive screen
// TODO [#1473]: https://github.com/Electric-Coin-Company/zashi-android/issues/1473
private fun BooleanArray.toBlackAndWhiteColorArray() =
    IntArray(size) {
        if (this[it]) {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }
