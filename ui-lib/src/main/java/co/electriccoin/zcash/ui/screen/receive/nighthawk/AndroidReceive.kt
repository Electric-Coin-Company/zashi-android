package co.electriccoin.zcash.ui.screen.receive.nighthawk

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.receive.nighthawk.view.ReceiveView

@Composable
internal fun MainActivity.AndroidReceive(onBack: () -> Unit) {
    WrapReceive(onBack = onBack)
}

@Composable
internal fun WrapReceive(onBack: () -> Unit) {
    ReceiveView(onBack = onBack)
}
