package co.electriccoin.zcash.ui.screen.signkeystonetransaction.state

import androidx.annotation.DrawableRes
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.StringResource

data class SignKeystoneTransactionState(
    val onBack: () -> Unit,
    val accountInfo: ZashiAccountInfoListItemState,
    val qrData: String,
    val generateNextQrCode: () -> Unit,
    val positiveButton: ButtonState,
    val negativeButton: ButtonState,
)

data class ZashiAccountInfoListItemState(
    @DrawableRes val icon: Int,
    val title: StringResource,
    val subtitle: StringResource,
)
