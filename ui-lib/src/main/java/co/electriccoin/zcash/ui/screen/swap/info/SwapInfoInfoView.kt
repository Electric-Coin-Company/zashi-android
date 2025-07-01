package co.electriccoin.zcash.ui.screen.swap.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapInfoInfoView(state: SwapInfoState) {
    ZashiScreenModalBottomSheet(state) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text =
                        when (it.mode) {
                            SWAP -> "Swap with"
                            PAY -> "Pay with"
                        },
                    style = ZashiTypography.textXl,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                Spacer(10.dp)
                Image(
                    painter = painterResource(R.drawable.ic_near_logo),
                    contentDescription = null
                )
            }
            Spacer(24.dp)
            Text(
                text =
                    when (it.mode) {
                        SWAP ->
                            "Swap from shielded ZEC to any NEAR-supported coin or token.\n\nZashi is a ZEC-only " +
                                "wallet, so you’ll need a valid wallet address for the asset you’re swapping to."
                        PAY ->
                            "Make cross-chain payments in any NEAR-supported coin or token.\n\nIf a payment should " +
                                "result in smaller output amount than you set, you will be refunded. "
                    },
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )
            Spacer(32.dp)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state =
                    ButtonState(
                        text = stringRes(R.string.general_ok),
                        onClick = state.onBack
                    )
            )
        }
    }
}

@PreviewScreens
@Composable
private fun SwapPreview() =
    ZcashTheme {
        SwapInfoInfoView(
            state =
                SwapInfoState(
                    mode = SWAP,
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun PayPreview() =
    ZcashTheme {
        SwapInfoInfoView(
            state =
                SwapInfoState(
                    mode = PAY,
                    onBack = {}
                )
        )
    }
