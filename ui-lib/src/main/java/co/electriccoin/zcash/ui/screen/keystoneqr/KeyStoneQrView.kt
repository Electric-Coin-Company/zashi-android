package co.electriccoin.zcash.ui.screen.keystoneqr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.screen.qrcode.util.AndroidQrCodeImageGenerator
import co.electriccoin.zcash.ui.screen.qrcode.util.JvmQrCodeGenerator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun KeystoneQrView(state: KeystoneQrState) {
    val qrSizePx = with(LocalDensity.current) { DEFAULT_QR_CODE_SIZE.roundToPx() }
    val bitmap = remember(state.qrData) { mutableStateOf<ImageBitmap?>(getQrCode(state.qrData, qrSizePx)) }

    bitmap.value?.let {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(DEFAULT_QR_CODE_SIZE)
        ) {
            Image(
                bitmap = it,
                contentDescription = "",
            )
        }
    }

    LaunchedEffect(state.qrData) {
        delay(100.milliseconds)
        state.generateNextQrCode()
    }
}

private fun getQrCode(address: String, size: Int): ImageBitmap {
    val qrCodePixelArray = JvmQrCodeGenerator.generate(address, size)
    return AndroidQrCodeImageGenerator.generate(qrCodePixelArray, size)
}

private val DEFAULT_QR_CODE_SIZE = 320.dp
