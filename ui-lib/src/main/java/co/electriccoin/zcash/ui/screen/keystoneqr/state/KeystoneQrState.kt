package co.electriccoin.zcash.ui.screen.keystoneqr.state

data class KeystoneQrState(
    val qrData: String,
    val generateNextQrCode: () -> Unit
)
