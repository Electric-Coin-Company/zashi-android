package co.electriccoin.zcash.ui.screen.qrdialog

import kotlinx.serialization.Serializable

@Serializable
data class QrDialog(
    val qr: String,
)
