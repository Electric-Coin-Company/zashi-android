package co.electriccoin.zcash.ui.design.component

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import co.electriccoin.zcash.ui.design.LocalKeyboardManager
import co.electriccoin.zcash.ui.design.LocalSheetStateManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : ModalBottomSheetState> ZashiScreenModalBottomSheet(
    state: T?,
    includeBottomPadding: Boolean = true,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
    dragHandle: @Composable (() -> Unit)? = { ZashiModalBottomSheetDragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.(state: T) -> Unit = {},
) {
    val parent = LocalView.current.parent
    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    state?.let {
        ZashiModalBottomSheet(
            sheetState = sheetState,
            dragHandle = dragHandle,
            content = {
                BackHandler {
                    it.onBack()
                }
                content(it)
                if (includeBottomPadding) {
                    Spacer(24.dp)
                    androidx.compose.foundation.layout.Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars),
                    )
                }

                LaunchedEffect(Unit) {
                    sheetState.show()
                }

                HookupKeyboardController()
            },
            onDismissRequest = it.onBack,
            contentWindowInsets = contentWindowInsets
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZashiScreenModalBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
    content: @Composable () -> Unit = {},
) {
    ZashiScreenModalBottomSheet(
        state =
            remember(onDismissRequest) {
                object : ModalBottomSheetState {
                    override val onBack: () -> Unit = {
                        onDismissRequest()
                    }
                }
            },
        sheetState = sheetState,
        content = {
            content()
            HookupKeyboardController()
        },
    )
}

@Composable
private fun HookupKeyboardController() {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val keyboardManager = LocalKeyboardManager.current
    DisposableEffect(softwareKeyboardController, keyboardManager) {
        if (softwareKeyboardController != null) {
            keyboardManager.onDialogOpened(softwareKeyboardController)
        }

        onDispose {
            if (softwareKeyboardController != null) {
                keyboardManager.onDialogClosed(softwareKeyboardController)
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun rememberScreenModalBottomSheetState(
    initialValue: SheetValue = if (LocalInspectionMode.current) Expanded else Hidden,
    skipHiddenState: Boolean = LocalInspectionMode.current,
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
