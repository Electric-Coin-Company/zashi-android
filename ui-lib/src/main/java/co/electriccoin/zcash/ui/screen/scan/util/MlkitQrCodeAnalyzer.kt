package co.electriccoin.zcash.ui.screen.scan.util

import android.graphics.ImageFormat
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.screen.scankeystone.view.FramePosition
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer

class MlkitQrCodeAnalyzer(
    private val framePosition: FramePosition,
    private val onQrCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val supportedImageFormat = Barcode.FORMAT_QR_CODE

    //
    // private val options = BarcodeScannerOptions.Builder()
    //     .setBarcodeFormats(supportedImageFormat)
    //     //.setZoomSuggestionOptions() // we could optionally use this to enhance scan success ratio
    //     .build()
    //
    // @OptIn(ExperimentalGetImage::class)
    // override fun analyze(imageProxy: ImageProxy) {
    //     Twig.error { "MLKIT PROXY: $imageProxy" }
    //
    //     val mediaImage = imageProxy.image
    //
    //     if (mediaImage != null) {
    //     // mediaImage.use {
    //         Twig.error { "MLKIT ANALYZER: $mediaImage" }
    //
    //         if (mediaImage.format == supportedImageFormat) {
    //             val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    //
    //             Twig.verbose {
    //                 "Scan result: " +
    //                     "Frame: $framePosition, "
    //                     // "Info: ${mediaImage.imageInfo}, " +
    //                     // "Image width: ${image.width}, " +
    //                     // "Image height: ${image.height}"
    //             }
    //
    //             // TODO [#1380]: Leverage FramePosition in QrCodeAnalyzer
    //             // TODO [#1380]: https://github.com/Electric-Coin-Company/zashi-android/issues/1380
    //             // val source =
    //             //     if (image.height > image.width) {
    //             //         PlanarYUVLuminanceSource(
    //             //             // yuvData =
    //             //             bytes,
    //             //             // dataWidth =
    //             //             image.width,
    //             //             // dataHeight =
    //             //             image.height,
    //             //             // left =
    //             //             (image.width * LEFT_OFFSET).toInt(),
    //             //             // top =
    //             //             (image.height * TOP_OFFSET).toInt(),
    //             //             // width =
    //             //             (image.width * WIDTH_OFFSET).toInt(),
    //             //             // height =
    //             //             (image.height * HEIGHT_OFFSET).toInt(),
    //             //             // reverseHorizontal =
    //             //             false
    //             //         )
    //             //     } else {
    //             //         PlanarYUVLuminanceSource(
    //             //             // yuvData =
    //             //             bytes,
    //             //             // dataWidth =
    //             //             image.width,
    //             //             // dataHeight =
    //             //             image.height,
    //             //             // left =
    //             //             (image.width * TOP_OFFSET).toInt(),
    //             //             // top =
    //             //             (image.height * LEFT_OFFSET).toInt(),
    //             //             // width =
    //             //             (image.width * HEIGHT_OFFSET).toInt(),
    //             //             // height =
    //             //             (image.height * WIDTH_OFFSET).toInt(),
    //             //             // reverseHorizontal =
    //             //             false
    //             //         )
    //             //     }
    //             //
    //             // val binaryBmp = BinaryBitmap(HybridBinarizer(source))
    //
    //             // Twig.verbose {
    //             //     "Scan result cropped: " +
    //             //         "Image width: ${binaryBmp.width}, " +
    //             //         "Image height: ${binaryBmp.height}"
    //             // }
    //
    //             runCatching {
    //                 // val result =
    //                 //     MultiFormatReader().apply {
    //                 //         setHints(
    //                 //             mapOf(
    //                 //                 DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
    //                 //                 DecodeHintType.ALSO_INVERTED to true
    //                 //             )
    //                 //         )
    //                 //     }.decodeWithState(binaryBmp)
    //
    //                 val scanner = BarcodeScanning.getClient(options)
    //
    //                 val result = scanner.process(image)
    //                     .addOnSuccessListener { barcodes ->
    //                         Twig.error { "MLKIT SUCCESS: $barcodes" }
    //                         onQrCodeScanned(barcodes[0].rawValue!!)
    //                         // Task completed successfully
    //                         // ...
    //                     }
    //                     .addOnFailureListener { error ->
    //                         Twig.error { "MLKIT ERROR: $error" }
    //                         // Task failed with an exception
    //                         // ...
    //                     }
    //             }.onFailure { failure ->
    //                 // failed to found QR code in current frame
    //                 Twig.error { "MLKIT FAILURE: $failure" }
    //             }
    //         }
    //     }
    // // }
    // }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Configure Barcode Scanner Options
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(supportedImageFormat)
                .build()

            // Initialize Barcode Scanner
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            Twig.error { "Detected: $value" }
                            onQrCodeScanned(value) // Callback for detected barcode
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Twig.error(e) { "Barcode detection failed" }
                }
                .addOnCompleteListener {
                    imageProxy.close() // Close the image proxy
                }
        } else {
            imageProxy.close()
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