package co.electriccoin.zcash.ui.screen.receiveqrcodes

sealed class QRAddressPagerItem(val title: String, val body: String, val buttonText: String) {
    data class UNIFIED(val addressType: String, val address: String, val btnText: String) : QRAddressPagerItem(addressType, address, btnText)
    data class SHIELDED(val addressType: String, val address: String, val btnText: String) : QRAddressPagerItem(addressType, address, btnText)
    data class TRANSPARENT(val addressType: String, val address: String, val btnText: String) : QRAddressPagerItem(addressType, address, btnText)
    data class TOP_UP(val titleText: String, val bodyText: String, val btnText: String) : QRAddressPagerItem(titleText, bodyText, btnText)
}