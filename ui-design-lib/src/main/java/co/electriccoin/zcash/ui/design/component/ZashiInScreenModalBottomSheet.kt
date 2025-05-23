package co.electriccoin.zcash.ui.design.component

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : ModalBottomSheetState> ZashiInScreenModalBottomSheet(
    state: T?,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberInScreenModalBottomSheetState(),
    dragHandle: @Composable (() -> Unit)? = { ZashiModalBottomSheetDragHandle() },
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
            properties =
                ModalBottomSheetProperties(
                    shouldDismissOnBackPress = false
                ),
            dragHandle = dragHandle
        ) {
            BackHandler {
                it.onBack()
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

@Composable
@ExperimentalMaterial3Api
fun rememberInScreenModalBottomSheetState(
    initialValue: SheetValue = if (LocalInspectionMode.current) Expanded else Hidden,
    skipHiddenState: Boolean = LocalInspectionMode.current,
    skipPartiallyExpanded: Boolean = true,
    confirmValueChange: (SheetValue) -> Boolean = { true },
) = rememberSheetState(
    skipPartiallyExpanded = skipPartiallyExpanded,
    confirmValueChange = confirmValueChange,
    initialValue = initialValue,
    skipHiddenState = skipHiddenState,
)

interface ModalBottomSheetState {
    val onBack: () -> Unit
}
