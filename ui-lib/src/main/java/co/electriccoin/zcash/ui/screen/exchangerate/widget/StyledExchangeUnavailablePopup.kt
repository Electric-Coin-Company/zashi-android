package co.electriccoin.zcash.ui.screen.exchangerate.widget

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.ZashiAnimatedTooltip
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
internal fun StyledExchangeUnavailablePopup(
    offset: IntOffset,
    transitionState: MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismissRequest,
        offset = offset
    ) {
        ZashiAnimatedTooltip(
            visibleState = transitionState,
            title = stringRes(R.string.exchange_rate_unavailable_title),
            message = stringRes(R.string.exchange_rate_unavailable_subtitle),
            onDismissRequest = onDismissRequest
        )
    }
}
