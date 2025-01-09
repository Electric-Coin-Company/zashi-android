package co.electriccoin.zcash.ui.design.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object AndroidQrCodeImageGenerator : QrCodeImageGenerator {
    override fun generate(
        bitArray: BooleanArray,
        sizePixels: Int,
        colors: QrCodeColors
    ): ImageBitmap {
        val colorArray = bitArray.toThemeColorArray(colors)

        return Bitmap.createBitmap(colorArray, sizePixels, sizePixels, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}

private fun BooleanArray.toThemeColorArray(colors: QrCodeColors) =
    IntArray(size) {
        if (this[it]) {
            colors.foreground
        } else {
            colors.background
        }
    }

data class QrCodeColors(
    val background: Int,
    val foreground: Int
) {
    companion object {
        val LightTheme = QrCodeColors(Color.WHITE, Color.BLACK)

        // The background color refers to [co.electriccoin.zcash.ui.design.theme.colors.Base.Obsidian]
        val DarkTheme = QrCodeColors(0xFF231F20.toInt(), Color.WHITE)
    }
}
