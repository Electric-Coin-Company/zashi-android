package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.design.util.stringRes

object ZashiMainTopAppBarStateFixture {
    fun new(
        onBalanceClick: () -> Unit = {},
        onSettingsClick: () -> Unit = {}
    ) = ZashiMainTopAppBarState(
        accountType = ZashiMainTopAppBarState.AccountType.ZASHI,
        balanceVisibilityButton =
            IconButtonState(
                icon = R.drawable.ic_hide_balances_on,
                contentDescription = stringRes(co.electriccoin.zcash.ui.R.string.hide_balances_content_description),
                onClick = onBalanceClick
            ),
        settingsButton =
            IconButtonState(
                icon = R.drawable.ic_app_bar_settings,
                contentDescription = stringRes(co.electriccoin.zcash.ui.R.string.settings_menu_content_description),
                onClick = onSettingsClick
            ),
        onAccountTypeClick = {}
    )
}
