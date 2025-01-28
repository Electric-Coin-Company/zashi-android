package co.electriccoin.zcash.ui.screen.transactionfilters.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiChipButton
import co.electriccoin.zcash.ui.design.component.ZashiChipButtonState
import co.electriccoin.zcash.ui.design.component.ZashiModalBottomSheet
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.screen.transactionfilters.fixture.TransactionFiltersStateFixture
import co.electriccoin.zcash.ui.screen.transactionfilters.model.TransactionFiltersState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TransactionFiltersView(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    state: TransactionFiltersState?
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
@OptIn(ExperimentalLayoutApi::class)
private fun BottomSheetContent(state: TransactionFiltersState?) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(co.electriccoin.zcash.ui.R.string.transaction_filters_title),
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(Modifier.height(24.dp))

        if (state == null) {
            CircularScreenProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.filters.forEach { filter ->
                        ZashiChipButton(
                            state = ZashiChipButtonState(
                                endIcon = if (filter.isSelected) {
                                    R.drawable.ic_x_close
                                } else {
                                    null
                                },
                                onClick = filter.onClick,
                                text = filter.text,
                            ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZashiButton(
                    state = state.secondaryButton,
                    modifier = Modifier.weight(1f),
                    colors = ZashiButtonDefaults.secondaryColors(
                        borderColor = ZashiColors.Btns.Secondary.btnSecondaryBorder
                    )
                )

                ZashiButton(
                    state = state.primaryButton,
                    modifier = Modifier.weight(1f),
                    colors = ZashiButtonDefaults.primaryColors()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        TransactionFiltersView(
            state = TransactionFiltersStateFixture.new(),
            onDismissRequest = {},
            sheetState =
                rememberModalBottomSheetState(
                    skipHiddenState = true,
                    skipPartiallyExpanded = true,
                    initialValue = SheetValue.Expanded,
                    confirmValueChange = { true }
                )
        )
    }


