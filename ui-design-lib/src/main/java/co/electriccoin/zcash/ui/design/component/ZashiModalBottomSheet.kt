package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZashiModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    sheetState: SheetState = rememberModalBottomSheetState(),
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    dragHandle: @Composable (() -> Unit)? = { ZashiModalBottomSheetDragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.statusBarsPadding(),
        sheetState = sheetState,
        scrimColor = scrimColor,
        shape = ZashiModalBottomSheetDefaults.SheetShape,
        containerColor = ZashiModalBottomSheetDefaults.ContainerColor,
        dragHandle = dragHandle,
        properties = properties,
        content = content,
        contentWindowInsets = contentWindowInsets
    )
}

@Composable
fun ZashiModalBottomSheetDragHandle() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .height(6.dp)
                    .width(42.dp)
                    .background(ZashiColors.Surfaces.bgQuaternary, CircleShape)
        )
    }
}

@Composable
@ExperimentalMaterial3Api
fun rememberModalBottomSheetState(
    initialValue: SheetValue = Hidden,
    skipHiddenState: Boolean = false,
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
) = rememberSheetState(
    skipPartiallyExpanded = skipPartiallyExpanded,
    confirmValueChange = confirmValueChange,
    initialValue = initialValue,
    skipHiddenState = skipHiddenState,
)

@Composable
@ExperimentalMaterial3Api
fun rememberSheetState(
    skipPartiallyExpanded: Boolean,
    confirmValueChange: (SheetValue) -> Boolean,
    initialValue: SheetValue,
    skipHiddenState: Boolean,
): SheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded,
        confirmValueChange,
        skipHiddenState,
        saver =
            SheetState.Saver(
                skipPartiallyExpanded = skipPartiallyExpanded,
                confirmValueChange = confirmValueChange,
                density = density,
                skipHiddenState = skipHiddenState,
            )
    ) {
        SheetState(
            skipPartiallyExpanded,
            density,
            initialValue,
            confirmValueChange,
            skipHiddenState,
        )
    }
}

object ZashiModalBottomSheetDefaults {
    val SheetShape: RoundedCornerShape
        get() = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val ContainerColor: Color
        @Composable
        get() = ZashiColors.Surfaces.bgPrimary
    val ContentColor: Color
        @Composable
        get() = ZashiColors.Text.textPrimary
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun ZashiModalBottomSheetPreview() =
    ZcashTheme {
        val sheetState =
            rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { false },
                skipHiddenState = true,
                initialValue = Expanded
            )

        LaunchedEffect(Unit) { sheetState.show() }

        ZashiModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState
        ) {
            Text("Content")
        }
    }
