package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZashiBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    sheetSwipeEnabled: Boolean = true,
    topBar: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    containerColor: Color = ZashiColors.Surfaces.bgPrimary,
    contentColor: Color = ZashiColors.Text.textPrimary,
    content: @Composable ((PaddingValues) -> Unit)? = null
) {
    BottomSheetScaffold(
        sheetContent = sheetContent,
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetShape = ZashiModalBottomSheetDefaults.SheetShape,
        sheetContainerColor = ZashiModalBottomSheetDefaults.ContainerColor,
        sheetContentColor = ZashiModalBottomSheetDefaults.ContentColor,
        sheetDragHandle = { ZashiModalBottomSheetDragHandle() },
        sheetSwipeEnabled = sheetSwipeEnabled,
        topBar = topBar,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content ?: { },
    )
}