package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.design.util.JvmQrCodeGenerator
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun ZashiQr(
    qrData: String,
    modifier: Modifier = Modifier,
    qrSize: Dp = ZashiQrDefaults.width
) {
    val qrSizePx = with(LocalDensity.current) { qrSize.roundToPx() }
    val bitmap = getQrCode(qrData, qrSizePx)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ZashiDimensions.Radius.radius4xl),
        border = BorderStroke(width = 1.dp, color = ZashiColors.Surfaces.strokePrimary),
        color = ZashiColors.Surfaces.bgPrimary orDark ZashiColors.Surfaces.bgAlt
    ) {
        Box(
            modifier = Modifier.padding(all = 6.dp)
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = "",
            )
        }
    }
}

private fun getQrCode(address: String, size: Int): ImageBitmap {
    val qrCodePixelArray = JvmQrCodeGenerator.generate(address, size)
    return AndroidQrCodeImageGenerator.generate(qrCodePixelArray, size)
}

object ZashiQrDefaults {
    val width: Dp
        @Composable
        get() = (LocalConfiguration.current.screenWidthDp * 0.66).dp
}