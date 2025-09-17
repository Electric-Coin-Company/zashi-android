package co.electriccoin.zcash.ui.design.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable

@Composable
infix fun <T> T.orHidden(hidden: T): T = if (LocalBalancesAvailable.current) this else hidden

@Composable
infix fun <T : StringResource> T.orHiddenString(hidden: T): String = (this orHidden hidden).getValue()

@Composable
infix fun <T : StyledStringResource> T.orHiddenString(hidden: StringResource) = (this.getValue() orHidden
    buildAnnotatedString { append(hidden.getValue()) })
