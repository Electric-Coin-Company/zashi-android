package co.electriccoin.zcash.ui.screen.integrations.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ZashiModalBottomSheet
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.model.IntegrationsState
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun IntegrationsDialogView(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    state: IntegrationsState
) {
    ZashiModalBottomSheet(
        sheetState = sheetState,
        content = {
            BottomSheetContent(state)
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun BottomSheetContent(state: IntegrationsState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(R.string.integrations_dialog_more_options),
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(8.dp))
        IntegrationItems(state, contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun IntegrationSettings() =
    ZcashTheme {
        IntegrationsDialogView(
            onDismissRequest = {},
            sheetState =
                rememberModalBottomSheetState(
                    skipHiddenState = true,
                    skipPartiallyExpanded = true,
                    initialValue = SheetValue.Expanded,
                    confirmValueChange = { true }
                ),
            state =
                IntegrationsState(
                    onBack = {},
                    disabledInfo = stringRes("Disabled info"),
                    items =
                        persistentListOf(
                            ZashiListItemState(
                                icon = R.drawable.ic_integrations_coinbase,
                                title = stringRes("Coinbase"),
                                subtitle = stringRes("subtitle"),
                                onClick = {}
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.integrations_flexa),
                                subtitle = stringRes(R.string.integrations_flexa),
                                icon = R.drawable.ic_integrations_flexa,
                                onClick = {}
                            ),
                            ZashiListItemState(
                                title = stringRes(R.string.integrations_keystone),
                                subtitle = stringRes(R.string.integrations_keystone_subtitle),
                                icon = R.drawable.ic_integrations_keystone,
                                onClick = {}
                            ),
                        ),
                    onBottomSheetHidden = {}
                ),
        )
    }
