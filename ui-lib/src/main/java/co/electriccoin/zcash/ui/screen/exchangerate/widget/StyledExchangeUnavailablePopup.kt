package co.electriccoin.zcash.ui.screen.exchangerate.widget

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.ZashiTooltip
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
internal fun StyledExchangeUnavailablePopup(
    onDismissRequest: () -> Unit,
) {
    ZashiTooltip(
        title = stringRes(R.string.exchange_rate_unavailable_title),
        message = stringRes(R.string.exchange_rate_unavailable_subtitle),
        onDismissRequest = onDismissRequest
    )
}
