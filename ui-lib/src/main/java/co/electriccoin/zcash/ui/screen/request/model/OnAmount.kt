package co.electriccoin.zcash.ui.screen.request.model

internal sealed class OnAmount(open val currency: RequestCurrency) {
    data class Number(val number: Int, override val currency: RequestCurrency) : OnAmount(currency)
    data class Separator(val separator: String, override val currency: RequestCurrency) : OnAmount(currency)
    data class Delete(override val currency: RequestCurrency) : OnAmount(currency)
}