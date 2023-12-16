package co.electriccoin.zcash.ui.screen.home.view

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.test.getStringResource

class HomeViewTest {
    // TODO [#1126]: Home screen view: Add view test
    // TODO [#1126]: https://github.com/Electric-Coin-Company/zashi-android/issues/1126
}

private fun ComposeContentTestRule.clickAccount() {
    onNodeWithText(getStringResource(R.string.home_tab_account), ignoreCase = true).also {
        it.performClick()
    }
}
private fun ComposeContentTestRule.clickSend() {
    onNodeWithText(getStringResource(R.string.home_tab_send), ignoreCase = true).also {
        it.performScrollTo()
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickReceive() {
    onNodeWithText(getStringResource(R.string.home_tab_receive), ignoreCase = true).also {
        it.performClick()
    }
}

private fun ComposeContentTestRule.clickBalances() {
    onNodeWithText(getStringResource(R.string.home_tab_balances), ignoreCase = true).also {
        it.performClick()
    }
}