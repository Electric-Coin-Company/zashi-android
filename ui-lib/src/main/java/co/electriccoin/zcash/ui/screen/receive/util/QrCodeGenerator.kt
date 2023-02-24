package co.electriccoin.zcash.ui.screen.receive.util

interface QrCodeGenerator {
    /**
     * @param data Data to encode into the QR code.
     * @param sizePixels Size in pixels of the QR code.
     * @return A QR code pixel matrix, represented as an array of booleans where false is white and true is black.
     */
    fun generate(data: String, sizePixels: Int): BooleanArray
}
