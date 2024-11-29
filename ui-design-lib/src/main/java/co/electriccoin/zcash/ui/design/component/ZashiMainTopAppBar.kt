package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun ZashiMainTopAppBar(state: ZashiMainTopAppBarState) {
    ZashiSmallTopAppBar(
        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        hamburgerMenuActions = {
            ZashiIconButton(state.balanceVisibilityButton)
            Spacer(Modifier.width(4.dp))
            ZashiIconButton(state.settingsButton)
            Spacer(Modifier.width(20.dp))
        },
        navigationAction = {
            Row(
                modifier =
                    Modifier
                        .defaultMinSize(40.dp, 40.dp)
                        .padding(start = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            onClick =
                                state
                                    .onAccountTypeClick
                        )
                        .padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter =
                        painterResource(
                            when (state.accountType) {
                                ZashiMainTopAppBarState.AccountType.ZASHI -> R.drawable.ic_item_zashi
                                ZashiMainTopAppBarState.AccountType.KEYSTONE -> R.drawable.ic_item_keystone
                            }
                        ),
                    contentDescription = null
                )
                Spacer(Modifier.width(4.dp))
                Image(
                    modifier =
                    when (state.accountType) {
                        ZashiMainTopAppBarState.AccountType.ZASHI -> Modifier.padding(bottom = 4.dp)
                        ZashiMainTopAppBarState.AccountType.KEYSTONE -> Modifier.padding(top = 4.dp)
                    },
                    painter =
                        painterResource(
                            when (state.accountType) {
                                ZashiMainTopAppBarState.AccountType.ZASHI -> R.drawable.ic_app_bar_zashi
                                ZashiMainTopAppBarState.AccountType.KEYSTONE -> R.drawable.ic_app_bar_keystone
                            }
                        ),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(R.drawable.ic_app_bar_arrow_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(ZashiColors.Btns.Ghost.btnGhostFg)
                )
            }
        },
    )
}

data class ZashiMainTopAppBarState(
    val onAccountTypeClick: () -> Unit,
    val accountType: AccountType,
    val balanceVisibilityButton: IconButtonState,
    val settingsButton: IconButtonState,
) {
    enum class AccountType {
        ZASHI,
        KEYSTONE
    }
}

@PreviewScreens
@Composable
private fun ZashiMainTopAppBarPreview() =
    ZcashTheme {
        ZashiMainTopAppBar(
            state =
                ZashiMainTopAppBarState(
                    accountType = ZashiMainTopAppBarState.AccountType.ZASHI,
                    balanceVisibilityButton = IconButtonState(R.drawable.ic_app_bar_balances_hide) {},
                    settingsButton = IconButtonState(R.drawable.ic_app_bar_settings) {},
                    onAccountTypeClick = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun KeystoneMainTopAppBarPreview() =
    ZcashTheme {
        ZashiMainTopAppBar(
            state =
            ZashiMainTopAppBarState(
                accountType = ZashiMainTopAppBarState.AccountType.KEYSTONE,
                balanceVisibilityButton = IconButtonState(R.drawable.ic_app_bar_balances_hide) {},
                settingsButton = IconButtonState(R.drawable.ic_app_bar_settings) {},
                onAccountTypeClick = {}
            )
        )
    }
