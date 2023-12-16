@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.balances

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.screen.balances.view.Balances

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun WrapBalances(
    activity: ComponentActivity,
    goSettings: () -> Unit,
) {
    Balances(
        onSettings = goSettings,
    )
}
