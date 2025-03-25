package co.electriccoin.zcash.ui.screen.restore.info

import android.view.WindowManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidSeedInfo() {
    val parent = LocalView.current.parent
    val navigationRouter = koinInject<NavigationRouter>()
    val scope = rememberCoroutineScope()

    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    SeedInfoView(
        sheetState = sheetState,
        state =
            SeedInfoState(
                onBack = {
                    scope.launch {
                        sheetState.hide()
                        navigationRouter.back()
                    }
                }
            ),
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                navigationRouter.back()
            }
        }
    )

    LaunchedEffect(Unit) {
        sheetState.show()
    }
}

@Serializable
object RestoreSeedInfo
