package co.electriccoin.zcash.ui.screen.keystoneqr

data class KeystoneQrState(
    val qrData: String,
    val generateNextQrCode: () -> Unit
)