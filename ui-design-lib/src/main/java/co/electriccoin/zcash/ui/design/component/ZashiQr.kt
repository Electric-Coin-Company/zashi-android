package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.design.util.QrCodeColors
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun ZashiQr(
    qrData: String,
    modifier: Modifier = Modifier,
    qrSize: Dp = ZashiQrDefaults.width,
    colors: QrCodeColors = QrCodeDefaults.colors(),
    contentDescription: String? = null,
    onQrCodeClick: () -> Unit = {},
    centerImage: Painter? = null
) {
    val qrSizePx = with(LocalDensity.current) { qrSize.roundToPx() }
    val bitmap = getQrCode(qrData, qrSizePx, colors)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ZashiDimensions.Radius.radius4xl),
        border = BorderStroke(width = 1.dp, color = ZashiColors.Surfaces.strokePrimary),
        color = ZashiColors.Surfaces.bgPrimary,
    ) {
        Box(
            modifier = Modifier.padding(all = 12.dp)
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                Modifier.clickable { onQrCodeClick() }
            )
            if (centerImage != null) {
                Image(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                    painter = centerImage,
                    contentDescription = contentDescription,
                )
            }
        }
    }
}

private fun getQrCode(
    address: String,
    size: Int,
    colors: QrCodeColors
): ImageBitmap {
    val qrCodePixelArray = JvmQrCodeGenerator.generate(address, size)
    return AndroidQrCodeImageGenerator.generate(qrCodePixelArray, size, colors)
}

object ZashiQrDefaults {
    val width: Dp
        @Composable
        get() = (LocalConfiguration.current.screenWidthDp * WIDTH_RATIO).dp
}

private const val WIDTH_RATIO = 0.66

object QrCodeDefaults {
    @Composable
    fun colors(
        background: Color = Color.White orDark ZashiColors.Surfaces.bgPrimary,
        foreground: Color = Color.Black orDark Color.White
    ) = QrCodeColors(
        background = background,
        foreground = foreground
    )
}
