package co.electriccoin.zcash.ui.design.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb

object AndroidQrCodeImageGenerator : QrCodeImageGenerator {
    override fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        colors: QrCodeColors
    ): ImageBitmap {
        val colorArray = bitArray.toThemeColorArray(
            foreground = colors.foreground.toArgb(),
            background = colors.background.toArgb()
        )
        return Bitmap
            .createBitmap(colorArray, sizePixels, sizePixels, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }

    override fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        background: Int,
        foreground: Int
    ): ImageBitmap {
        val colorArray = bitArray.toThemeColorArray(foreground = foreground, background = background)
        return Bitmap
            .createBitmap(colorArray, sizePixels, sizePixels, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}

private fun BooleanArray.toThemeColorArray(foreground: Int, background: Int) =
    IntArray(size) {
        if (this[it]) foreground else background
    }

data class QrCodeColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
)
