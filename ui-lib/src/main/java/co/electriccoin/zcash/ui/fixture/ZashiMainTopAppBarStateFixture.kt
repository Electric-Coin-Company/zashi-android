package co.electriccoin.zcash.ui.fixture

import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.ZashiMainTopAppBarState

object ZashiMainTopAppBarStateFixture {
    fun new() =
        ZashiMainTopAppBarState(
            accountType = ZashiMainTopAppBarState.AccountType.ZASHI,
            balanceVisibilityButton = IconButtonState(R.drawable.ic_app_bar_balances_hide) {},
            settingsButton = IconButtonState(R.drawable.ic_app_bar_settings) {},
            onAccountTypeClick = {}
        )
}
