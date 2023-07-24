package co.electriccoin.zcash.ui.screen.receiveqrcodes

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

sealed class QRAddressPagerItem(val title: String, val body: String, val buttonText: String, val logoId: Int, val backgroundColor: Color) {
    data class UNIFIED(val addressType: String, val address: String, val btnText: String, @DrawableRes val id: Int, val backGroundColor: Color) : QRAddressPagerItem(addressType, address, btnText, id, backGroundColor)
    data class SHIELDED(val addressType: String, val address: String, val btnText: String, @DrawableRes val id: Int, val backGroundColor: Color) : QRAddressPagerItem(addressType, address, btnText, id, backGroundColor)
    data class TRANSPARENT(val addressType: String, val address: String, val btnText: String, @DrawableRes val id: Int, val backGroundColor: Color) : QRAddressPagerItem(addressType, address, btnText, id, backGroundColor)
    data class TOP_UP(val titleText: String, val bodyText: String, val btnText: String, @DrawableRes val id: Int, val backGroundColor: Color) : QRAddressPagerItem(titleText, bodyText, btnText, id, backGroundColor)
}