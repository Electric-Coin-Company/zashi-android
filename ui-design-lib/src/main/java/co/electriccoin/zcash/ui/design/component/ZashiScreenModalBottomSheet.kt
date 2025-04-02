package co.electriccoin.zcash.ui.design.component

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import co.electriccoin.zcash.ui.design.LocalSheetStateManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : ModalBottomSheetState> ZashiScreenModalBottomSheet(
    state: T?,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
    content: @Composable () -> Unit = {},
) {
    ZashiModalBottomSheet(
        sheetState = sheetState,
        content = {
            BackHandler(state != null) {
                state?.onBack?.invoke()
            }
            content()
        },
        onDismissRequest = { state?.onBack?.invoke() }
    )

    LaunchedEffect(Unit) {
        sheetState.show()
    }
}

@Composable
@ExperimentalMaterial3Api
fun rememberScreenModalBottomSheetState(
    initialValue: SheetValue = Hidden,
    skipHiddenState: Boolean = false,
    skipPartiallyExpanded: Boolean = true,
    confirmValueChange: (SheetValue) -> Boolean = { true },
): SheetState {
    val sheetManager = LocalSheetStateManager.current
    val sheetState =
        rememberSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = confirmValueChange,
            initialValue = initialValue,
            skipHiddenState = skipHiddenState,
        )
    DisposableEffect(sheetState) {
        sheetManager.onSheetOpened(sheetState)
        onDispose {
            sheetManager.onSheetDisposed(sheetState)
        }
    }
    return sheetState
}
