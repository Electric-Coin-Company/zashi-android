package co.electriccoin.zcash.ui.common.appbar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun ZashiTopAppbar(
    state: ZashiMainTopAppBarState?,
    title: StringResource? = null,
    showHideBalances: Boolean = true,
    onBack: () -> Unit,
) {
    ZashiSmallTopAppBar(
        title = title?.getValue(),
        subtitle = state?.subtitle?.getValue(),
        navigationAction = {
            ZashiTopAppBarBackNavigation(onBack = onBack)
        },
        regularActions = {
            if (state?.balanceVisibilityButton != null && showHideBalances) {
                Crossfade(state.balanceVisibilityButton, label = "") {
                    ZashiIconButton(it, modifier = Modifier.size(40.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}
