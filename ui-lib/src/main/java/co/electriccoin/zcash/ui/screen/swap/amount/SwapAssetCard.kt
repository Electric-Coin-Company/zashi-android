package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun SwapAssetCard(
    state: SwapAssetCardState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = ZashiColors.Surfaces.bgPrimary,
        border = BorderStroke(.33.dp, ZashiColors.Surfaces.strokeSecondary),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier =
                Modifier.padding(
                    start = if (state.token is ImageResource.ByDrawable) 4.dp else 14.dp,
                    top = if (state.token is ImageResource.ByDrawable) 4.dp else 8.dp,
                    end = 12.dp,
                    bottom = if (state.token is ImageResource.ByDrawable) 4.dp else 8.dp,
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.token is ImageResource.ByDrawable) {
                Box {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(state.token.resource),
                        contentDescription = null
                    )

                    if (state.chain is ImageResource.ByDrawable) {
                        Image(
                            modifier =
                                Modifier
                                    .size(14.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(4.dp, 4.dp),
                            painter = painterResource(state.chain.resource),
                            contentDescription = null,
                        )
                    }
                }
                Spacer(8.dp)
            }
            Text(
                text = state.ticker.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Immutable
data class SwapAssetCardState(
    val ticker: StringResource,
    val token: ImageResource?,
    val chain: ImageResource?
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapAssetCard(
                state =
                    SwapAssetCardState(
                        ticker = stringRes("USDT"),
                        token = imageRes(R.drawable.ic_token_zec),
                        chain = imageRes(R.drawable.ic_chain_zec)
                    )
            )
        }
    }
