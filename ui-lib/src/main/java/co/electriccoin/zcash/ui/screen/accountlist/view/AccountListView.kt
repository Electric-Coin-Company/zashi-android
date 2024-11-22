package co.electriccoin.zcash.ui.screen.accountlist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.LottieProgress
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiModalBottomSheet
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItem
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AccountListView(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    state: AccountListState
) {
    ZashiModalBottomSheet(
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        content = {
            BottomSheetContent(state)
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
private fun ColumnScope.BottomSheetContent(state: AccountListState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Wallets & Hardware",
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier =
                Modifier
                    .weight(1f, false)
                    .verticalScroll(rememberScrollState())
        ) {
            state.accounts?.forEach {
                ZashiSettingsListItem(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    state = it,
                )
            }
            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                LottieProgress(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        ZashiButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            state = state.addWalletButton,
            colors =
                ZashiButtonDefaults.secondaryColors(
                    borderColor = ZashiColors.Btns.Secondary.btnSecondaryBorder
                )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        AccountListView(
            state =
                AccountListState(
                    accounts =
                        (1..20).map {
                            ZashiSettingsListItemState(
                                text = stringRes("title"),
                                subtitle = stringRes("subtitle"),
                                icon = R.drawable.ic_radio_button_checked
                            )
                        },
                    isLoading = true,
                    addWalletButton =
                        ButtonState(
                            text = stringRes("Add hardware wallet")
                        )
                ),
            onDismissRequest = {},
            sheetState =
                rememberModalBottomSheetState(
                    skipHiddenState = true,
                    initialValue = SheetValue.Expanded,
                    confirmValueChange = { false }
                )
        )
    }
