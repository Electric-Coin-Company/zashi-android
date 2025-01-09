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
        val colorArray = bitArray.toThemeColorArray(colors)

        return Bitmap.createBitmap(colorArray, sizePixels, sizePixels, Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }
}

private fun BooleanArray.toThemeColorArray(colors: QrCodeColors) =
    IntArray(size) {
        if (this[it]) {
            colors.foreground.toArgb()
        } else {
            colors.background.toArgb()
        }
    }

data class QrCodeColors(
    val background: Color,
    val foreground: Color
) {
    companion object {
        val LightTheme = QrCodeColors(Color.White, Color.Black)

        // The background color refers to [co.electriccoin.zcash.ui.design.theme.colors.Base.Obsidian]
        val DarkTheme = QrCodeColors(Color(0xFF231F20), Color.White)
    }
}
