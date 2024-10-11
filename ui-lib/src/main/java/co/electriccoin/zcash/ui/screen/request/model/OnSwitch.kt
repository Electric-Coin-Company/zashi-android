package co.electriccoin.zcash.ui.screen.request.model

internal sealed class OnSwitch {
    data object ToZec : OnSwitch()
    data object ToFiat : OnSwitch()
}