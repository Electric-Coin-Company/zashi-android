package co.electriccoin.zcash.ui.screen.integrations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.component.rememberScreenModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun IntegrationsDialogView(
    state: IntegrationsState?,
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
fun BottomSheetContent(state: IntegrationsState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(R.string.integrations_dialog_more_options),
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(8.dp)
        IntegrationItems(state, contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp))
        Spacer(16.dp)
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_info),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ZashiColors.Text.textTertiary)
            )
            Spacer(8.dp)
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.integrations_dialog_info),
                textAlign = TextAlign.Start,
                style = ZashiTypography.textXs,
                color = ZashiColors.Text.textTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun IntegrationSettings() =
    ZcashTheme {
        IntegrationsDialogView(
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
                            ListItemState(
                                bigIcon = imageRes(R.drawable.ic_integrations_coinbase),
                                title = stringRes("Coinbase"),
                                subtitle = stringRes("subtitle"),
                                onClick = {}
                            ),
                            ListItemState(
                                title = stringRes(R.string.integrations_flexa),
                                subtitle = stringRes(R.string.integrations_flexa),
                                bigIcon = imageRes(R.drawable.ic_integrations_flexa),
                                onClick = {}
                            ),
                            ListItemState(
                                title = stringRes(R.string.integrations_keystone),
                                subtitle = stringRes(R.string.integrations_keystone_subtitle),
                                bigIcon = imageRes(R.drawable.ic_integrations_keystone),
                                onClick = {}
                            ),
                        ),
                ),
        )
    }
