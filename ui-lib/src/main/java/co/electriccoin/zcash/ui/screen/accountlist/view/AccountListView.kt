package co.electriccoin.zcash.ui.screen.accountlist.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.LottieProgress
import co.electriccoin.zcash.ui.design.component.ZashiModalBottomSheet
import co.electriccoin.zcash.ui.design.component.listitem.BaseListItem
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemColors
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDesignType
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.component.rememberModalBottomSheetState
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListItem
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState
import co.electriccoin.zcash.ui.screen.accountlist.model.ZashiAccountListItemState
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AccountListView(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    state: AccountListState
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
private fun BottomSheetContent(state: AccountListState) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(co.electriccoin.zcash.ui.R.string.account_list_title),
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
            state.items?.forEachIndexed { index, item ->
                if (index != 0) {
                    Spacer(Modifier.height(8.dp))
                }

                when (item) {
                    is AccountListItem.Account ->
                        ZashiAccountListItem(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            state = item.state,
                        )

                    is AccountListItem.Other ->
                        ZashiKeystonePromoListItem(item)
                }
            }
            if (state.isLoading) {
                Spacer(Modifier.height(24.dp))
                LottieProgress(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@Composable
private fun ZashiKeystonePromoListItem(item: AccountListItem.Other) {
    ZashiKeystonePromoListItem(
        modifier = Modifier.padding(horizontal = 4.dp),
        state = item.state,
    )
}

@Composable
private fun ZashiKeystonePromoListItem(
    state: ZashiListItemState,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit = {
        ZashiKeystonePromoListContent(
            modifier = it,
            text = state.title.getValue(),
            subtitle = state.subtitle?.getValue(),
            isEnabled = state.isEnabled
        )
    },
    below: @Composable ColumnScope.(Modifier) -> Unit = {
        Image(
            painter = painterResource(co.electriccoin.zcash.ui.R.drawable.img_keystone_promo),
            contentDescription = null
        )
    },
    contentPadding: PaddingValues = PaddingValues(24.dp),
    colors: ZashiListItemColors =
        ZashiListItemDefaults.primaryColors(
            backgroundColor = ZashiColors.Surfaces.bgTertiary
        )
) {
    BaseListItem(
        modifier = modifier,
        contentPadding = contentPadding,
        leading = null,
        content = content,
        trailing = null,
        below = below,
        onClick = state.onClick.takeIf { state.isEnabled },
        border = colors.borderColor.takeIf { !it.isUnspecified }?.let { BorderStroke(1.dp, it) },
        color = colors.backgroundColor
    )
}

@Composable
private fun ZashiKeystonePromoListContent(
    text: String,
    subtitle: String?,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = ZashiTypography.textLg,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (isEnabled) {
                        ZashiColors.Text.textPrimary
                    } else {
                        ZashiColors.Text.textDisabled
                    }
            )
        }
        subtitle?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                style = ZashiTypography.textXs,
                color = ZashiColors.Text.textTertiary
            )
        }
    }
}

@Composable
private fun ZashiAccountListItem(
    state: ZashiAccountListItemState,
    modifier: Modifier = Modifier,
) {
    BaseListItem(
        modifier = modifier,
        contentPadding = ZashiListItemDefaults.contentPadding,
        leading = {
            ZashiListItemDefaults.LeadingItem(
                modifier = it,
                icon = state.icon,
                contentDescription = state.title.getValue()
            )
        },
        content = {
            ZashiListItemDefaults.ContentItem(
                modifier = it,
                text = state.title.getValue(),
                subtitle = state.subtitle.getValue(),
                titleIcons = persistentListOf(),
                isEnabled = true
            )
        },
        trailing = {
            // empty
        },
        color =
            if (state.isSelected) {
                ZashiColors.Surfaces.bgSecondary
            } else {
                Color.Transparent
            },
        onClick = state.onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        AccountListView(
            state =
                AccountListState(
                    items =
                        listOf(
                            AccountListItem.Account(
                                ZashiAccountListItemState(
                                    title = stringRes("Zashi"),
                                    subtitle = stringRes("u1078r23uvtj8xj6dpdx..."),
                                    icon = R.drawable.ic_item_zashi,
                                    isSelected = true,
                                    onClick = {}
                                )
                            ),
                            AccountListItem.Other(
                                ZashiListItemState(
                                    title = stringRes("Keystone Hardware Wallet"),
                                    subtitle = stringRes("Get a Keystone Hardware Wallet and secure your Zcash."),
                                    design = ZashiListItemDesignType.SECONDARY,
                                    onClick = {}
                                )
                            )
                        ),
                    isLoading = true,
                    onBottomSheetHidden = {},
                    onBack = {}
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
