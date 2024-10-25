package co.electriccoin.zcash.ui.screen.exchangerate.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.ZashiTooltip
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
        AnimatedVisibility(
            visibleState = transitionState,
            enter =
            fadeIn() +
                slideInVertically(spring(stiffness = Spring.StiffnessHigh)) +
                scaleIn(spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy)),
            exit =
            fadeOut() +
                scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                slideOutVertically(),
        ) {
            ZashiTooltip(
                title = stringRes(R.string.exchange_rate_unavailable_title),
                message = stringRes(R.string.exchange_rate_unavailable_subtitle),
                onDismissRequest = onDismissRequest
            )
        }
    }
}
