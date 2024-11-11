package co.electriccoin.zcash.ui.screen.scan.model

sealed class ScanResultState {
    data class Address(val address: String) : ScanResultState()

    data class Zip321Uri(val zip321Uri: String) : ScanResultState()
}
