package co.electriccoin.zcash.ui.screen.qrdialog

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.design.component.QrState
import org.koin.compose.koinInject

@Composable
fun AndroidQrDialog(arg: QrDialog) {
    val navigationRouter = koinInject<NavigationRouter>()

    val parent = LocalView.current.parent

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.setDimAmount(DIALOG_DIM)
    }

    BackHandler {
        navigationRouter.back()
    }

    QrDialogView(
        state =
            QrState(
                qrData = arg.qr,
            ),
        onBack = {
            navigationRouter.back()
        }
    )
}

private const val DIALOG_DIM = .8f
