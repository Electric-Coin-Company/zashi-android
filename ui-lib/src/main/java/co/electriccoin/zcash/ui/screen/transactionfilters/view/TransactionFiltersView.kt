package co.electriccoin.zcash.ui.screen.transactionfilters.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    state: TransactionFiltersState
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
private fun BottomSheetContent(state: TransactionFiltersState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(co.electriccoin.zcash.ui.R.string.transaction_filters_title),
            style = ZashiTypography.textXl,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
        // if (state.addWalletButton != null) {
        //     Spacer(modifier = Modifier.height(32.dp))
        //     ZashiButton(
        //         state = state.addWalletButton,
        //         modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        //         colors =
        //             ZashiButtonDefaults.secondaryColors(
        //                 borderColor = ZashiColors.Btns.Secondary.btnSecondaryBorder
        //             )
        //     )
        // }
        // Spacer(modifier = Modifier.height(24.dp))
        // Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}


// @Composable
// private fun ZashiKeystonePromoListItem(
//     state: ZashiListItemState,
//     modifier: Modifier = Modifier,
//     content: @Composable (Modifier) -> Unit = {
//         ZashiKeystonePromoListContent(
//             modifier = it,
//             text = state.title.getValue(),
//             subtitle = state.subtitle?.getValue(),
//             isEnabled = state.isEnabled
//         )
//     },
//     below: @Composable ColumnScope.(Modifier) -> Unit = {
//         Image(
//             painter = painterResource(co.electriccoin.zcash.ui.R.drawable.img_keystone_promo),
//             contentDescription = null
//         )
//     },
//     contentPadding: PaddingValues = PaddingValues(24.dp),
//     colors: ZashiListItemColors =
//         ZashiListItemDefaults.primaryColors(
//             backgroundColor = ZashiColors.Surfaces.bgTertiary
//         )
// ) {
//     BaseListItem(
//         modifier = modifier,
//         contentPadding = contentPadding,
//         leading = null,
//         content = content,
//         trailing = null,
//         below = below,
//         onClick = state.onClick.takeIf { state.isEnabled },
//         border = colors.borderColor.takeIf { !it.isUnspecified }?.let { BorderStroke(1.dp, it) },
//         color = colors.backgroundColor
//     )
// }
//
// @Composable
// private fun ZashiChipButtonState(
//     state: ZashiAccountListItemState,
//     modifier: Modifier = Modifier,
// ) {
//     BaseListItem(
//         modifier = modifier,
//         contentPadding = ZashiListItemDefaults.contentPadding,
//         leading = {
//             ZashiListItemDefaults.LeadingItem(
//                 modifier = it,
//                 icon = state.icon,
//                 contentDescription = state.title.getValue()
//             )
//         },
//         content = {
//             ZashiListItemDefaults.ContentItem(
//                 modifier = it,
//                 text = state.title.getValue(),
//                 subtitle = state.subtitle.getValue(),
//                 titleIcons = persistentListOf(),
//                 isEnabled = true
//             )
//         },
//         trailing = {
//             // empty
//         },
//         color =
//             if (state.isSelected) {
//                 ZashiColors.Surfaces.bgSecondary
//             } else {
//                 Color.Transparent
//             },
//         onClick = state.onClick
//     )
// }
//
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


