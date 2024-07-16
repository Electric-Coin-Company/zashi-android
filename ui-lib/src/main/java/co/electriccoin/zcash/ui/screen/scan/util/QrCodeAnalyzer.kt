package co.electriccoin.zcash.ui.screen.scan.util

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.scan.view.FramePosition
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(
    private val framePosition: FramePosition,
    private val onQrCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val supportedImageFormats =
        listOf(
            ImageFormat.YUV_420_888,
            ImageFormat.YUV_422_888,
            ImageFormat.YUV_444_888
        )

    override fun analyze(image: ImageProxy) {
        image.use {
            if (image.format in supportedImageFormats) {
                val bytes = image.planes.first().buffer.toByteArray()

                Twig.verbose {
                    "Scan result: " +
                        "Frame: $framePosition, " +
                        "Info: ${image.imageInfo}, " +
                        "Image width: ${image.width}, " +
                        "Image height: ${image.height}"
                }

                val source =
                    PlanarYUVLuminanceSource(
                        bytes,
                        image.width,
                        image.height,
                        0,
                        0,
                        image.width,
                        image.height,
                        false
                    )

                val binaryBmp = BinaryBitmap(HybridBinarizer(source))

                // TODO [#1380]: Leverage FramePosition in QrCodeAnalyzer
                // TODO [#1380]: https://github.com/Electric-Coin-Company/zashi-android/issues/1380
                @Suppress("MagicNumber")
                val binaryBitmapCropped =
                    binaryBmp.crop(
                        0,
                        (binaryBmp.height * 0.25).toInt(),
                        binaryBmp.width,
                        (binaryBmp.height * 0.66).toInt()
                    )

                Twig.verbose {
                    "Scan result cropped: " +
                        "Image width: ${binaryBitmapCropped.width}, " +
                        "Image height: ${binaryBitmapCropped.height}"
                }

                runCatching {
                    val result =
                        MultiFormatReader().apply {
                            setHints(
                                mapOf(
                                    DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
                                    DecodeHintType.ALSO_INVERTED to true
                                )
                            )
                        }.decodeWithState(binaryBitmapCropped)

                    onQrCodeScanned(result.text)
                }.onFailure {
                    // failed to found QR code in current frame
                }
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}
