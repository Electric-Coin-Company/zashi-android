package co.electriccoin.zcash.ui.screen.sendoptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SendOptionsView(
    state: SendOptionsState?,
    sheetState: SheetState = rememberScreenModalBottomSheetState(),
) {
    ZashiScreenModalBottomSheet(
        state = state,
        sheetState = sheetState,
        content = {
            BottomSheetContent(it)
        },
    )
}

@Composable
fun BottomSheetContent(state: SendOptionsState) {
    Column {
        state.items.forEachIndexed { index, item ->
            ZashiListItem(
                state = item,
                modifier = Modifier.padding(horizontal = 4.dp),
                leading = {
                    ZashiListItemDefaults.LeadingItem(
                        modifier = Modifier.size(40.dp),
                        icon = item.icon,
                        contentDescription = item.title.getValue()
                    )
                },
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            )
            if (index != state.items.lastIndex) {
                ZashiHorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        SendOptionsView(
            sheetState =
                rememberModalBottomSheetState(
                    skipHiddenState = true,
                    skipPartiallyExpanded = true,
                    initialValue = SheetValue.Expanded,
                    confirmValueChange = { true }
                ),
            state =
                SendOptionsState(
                    onBack = {},
                    items =
                        persistentListOf(
                            ListItemState(
                                title = stringRes("Pay in ZEC"),
                                subtitle = stringRes("Pay anyone by sending them ZEC"),
                                onClick = {}
                            ),
                            ListItemState(
                                title = stringRes("SWAPnPAy"),
                                subtitle = stringRes("Pay anyone by swapping ZEC with Near integration."),
                                onClick = {}
                            ),
                        ),
                ),
        )
    }
