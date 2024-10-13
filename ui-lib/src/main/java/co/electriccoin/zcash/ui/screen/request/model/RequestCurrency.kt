package co.electriccoin.zcash.ui.screen.request.model

internal sealed class RequestCurrency {
    data object Zec : RequestCurrency()
    data object Fiat : RequestCurrency()
}