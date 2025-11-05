package co.electriccoin.zcash.ui.screen.advancedsettings.debug.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun DebugTextScreen(args: DebugTextArgs) {
    val navigationRouter = koinInject<NavigationRouter>()
    val state = remember(args) {
        DebugTextState(
            title = stringRes(args.title),
            text = stringRes(args.text),
            onBack = { navigationRouter.back() },
        )
    }
    DebugTextView(state)
}

@Serializable
data class DebugTextArgs(
    val title: String,
    val text: String
)
