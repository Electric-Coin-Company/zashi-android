package co.electriccoin.zcash.ui.screen.scan.util

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.scankeystone.view.FramePosition
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

                // TODO [#1380]: Leverage FramePosition in QrCodeAnalyzer
                // TODO [#1380]: https://github.com/Electric-Coin-Company/zashi-android/issues/1380
                val source =
                    if (image.height > image.width) {
                        PlanarYUVLuminanceSource(
                            // yuvData =
                            bytes,
                            // dataWidth =
                            image.width,
                            // dataHeight =
                            image.height,
                            // left =
                            (image.width * LEFT_OFFSET).toInt(),
                            // top =
                            (image.height * TOP_OFFSET).toInt(),
                            // width =
                            (image.width * WIDTH_OFFSET).toInt(),
                            // height =
                            (image.height * HEIGHT_OFFSET).toInt(),
                            // reverseHorizontal =
                            false
                        )
                    } else {
                        PlanarYUVLuminanceSource(
                            // yuvData =
                            bytes,
                            // dataWidth =
                            image.width,
                            // dataHeight =
                            image.height,
                            // left =
                            (image.width * TOP_OFFSET).toInt(),
                            // top =
                            (image.height * LEFT_OFFSET).toInt(),
                            // width =
                            (image.width * HEIGHT_OFFSET).toInt(),
                            // height =
                            (image.height * WIDTH_OFFSET).toInt(),
                            // reverseHorizontal =
                            false
                        )
                    }

                val binaryBmp = BinaryBitmap(HybridBinarizer(source))

                Twig.verbose {
                    "Scan result cropped: " +
                        "Image width: ${binaryBmp.width}, " +
                        "Image height: ${binaryBmp.height}"
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
                        }.decodeWithState(binaryBmp)

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

private const val LEFT_OFFSET = .15
private const val TOP_OFFSET = .25
private const val WIDTH_OFFSET = .7
private const val HEIGHT_OFFSET = .45
