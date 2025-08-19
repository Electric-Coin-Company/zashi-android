package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Suppress("MagicNumber")
@Composable
fun ZashiSwapQuoteHeader(
    state: SwapQuoteHeaderState,
    balancesAvailable: Boolean = true
) {
    CompositionLocalProvider(
        LocalBalancesAvailable provides balancesAvailable
    ) {
        Box {
            Row {
                ZashiSwapQuoteAmount(modifier = Modifier.weight(1f), state = state.from)
                Spacer(8.dp)
                ZashiSwapQuoteAmount(modifier = Modifier.weight(1f), state = state.to)
            }
            if (state.rotateIcon != null) {
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = CircleShape,
                    color = ZashiColors.Surfaces.bgPrimary,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        Modifier.padding(8.dp)
                    ) {
                        Image(
                            modifier = Modifier.rotate(if (state.rotateIcon) 180f else 0f),
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Immutable
data class SwapQuoteHeaderState(
    val rotateIcon: Boolean?,
    val from: SwapTokenAmountState?,
    val to: SwapTokenAmountState?,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiSwapQuoteHeader(
                state =
                    SwapQuoteHeaderState(
                        rotateIcon = false,
                        from =
                            SwapTokenAmountState(
                                bigIcon = imageRes(R.drawable.ic_chain_placeholder),
                                smallIcon = imageRes(R.drawable.ic_receive_shield),
                                title = stringResByDynamicCurrencyNumber(0.000000421423154, "", TickerLocation.HIDDEN),
                                subtitle = stringResByDynamicCurrencyNumber(0.0000000000000021312, "$")
                            ),
                        to =
                            SwapTokenAmountState(
                                bigIcon = imageRes(R.drawable.ic_chain_placeholder),
                                smallIcon = imageRes(R.drawable.ic_receive_shield),
                                title = stringResByDynamicCurrencyNumber(0.000000421423154, "", TickerLocation.HIDDEN),
                                subtitle = stringResByDynamicCurrencyNumber(0.0000000000000021312, "$")
                            )
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiSwapQuoteHeader(
                state =
                    SwapQuoteHeaderState(
                        rotateIcon = false,
                        from = null,
                        to = null
                    )
            )
        }
    }
