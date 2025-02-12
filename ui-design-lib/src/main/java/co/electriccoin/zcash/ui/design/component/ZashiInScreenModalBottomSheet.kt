package co.electriccoin.zcash.ui.design.component

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : ModalBottomSheetState> ZashiInScreenModalBottomSheet(
    state: T?,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable (T) -> Unit = {},
) {
    var normalizedState: T? by remember { mutableStateOf(null) }

    normalizedState?.let {
        ZashiModalBottomSheet(
            onDismissRequest = {
                it.onBack()
            },
            modifier = modifier,
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false
            )
        ) {
            BackHandler(
                enabled = normalizedState != null
            ) {
                normalizedState?.onBack?.invoke()
            }

            content(it)
        }
    }

    LaunchedEffect(state) {
        if (state != null) {
            normalizedState = state
            sheetState.show()
        } else {
            sheetState.hide()
            normalizedState = null
        }
    }
}

interface ModalBottomSheetState {
    val onBack: () -> Unit
}
