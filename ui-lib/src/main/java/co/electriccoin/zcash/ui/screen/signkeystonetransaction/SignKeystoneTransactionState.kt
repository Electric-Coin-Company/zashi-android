package co.electriccoin.zcash.ui.screen.signkeystonetransaction

import androidx.annotation.DrawableRes
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.QrState
import co.electriccoin.zcash.ui.design.util.StringResource

data class SignKeystoneTransactionState(
    val onBack: () -> Unit,
    val accountInfo: ZashiAccountInfoListItemState,
    val qrData: String?,
    val generateNextQrCode: () -> Unit,
    val shareButton: ButtonState?,
    val positiveButton: ButtonState,
    val negativeButton: ButtonState,
) {
    fun toQrState(
        contentDescription: StringResource? = null,
        centerImageResId: Int? = null,
    ): QrState {
        requireNotNull(qrData) { "The QR code data needs to be set at this point" }
        return QrState(
            qrData = qrData,
            contentDescription = contentDescription,
            centerImageResId = centerImageResId
        )
    }
}

data class ZashiAccountInfoListItemState(
    @DrawableRes val icon: Int,
    val title: StringResource,
    val subtitle: StringResource,
)
