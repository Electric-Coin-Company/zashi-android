package co.electriccoin.zcash.ui.screen.request.model

internal sealed class OnAmount {
    data class Number(val number: Int) : OnAmount()

    data class Separator(val separator: String) : OnAmount()

    data object Delete : OnAmount()
}
