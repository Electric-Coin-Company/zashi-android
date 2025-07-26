package co.electriccoin.zcash.ui.screen.swap.quote

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiAutoSizeText
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Composable
fun SwapQuoteAmount(
    state: SwapTokenAmountState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ZashiColors.Surfaces.bgSecondary,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SelectionContainer {
                    ZashiAutoSizeText(
                        modifier = Modifier.weight(1f, false),
                        textAlign = TextAlign.Center,
                        text = state.title.getValue(),
                        style = ZashiTypography.textXl,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textPrimary,
                        maxLines = 1,
                    )
                }
                if (state.bigIcon is ImageResource.ByDrawable) {
                    Spacer(4.dp)
                    Box {
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(state.bigIcon.resource),
                            contentDescription = null
                        )

                        if (state.smallIcon is ImageResource.ByDrawable) {
                            if (state.smallIcon.resource == co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield) {
                                Image(
                                    modifier =
                                        Modifier
                                            .size(12.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(4.dp, 2.dp),
                                    painter = painterResource(state.smallIcon.resource),
                                    contentDescription = null,
                                )
                            } else {
                                Surface(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(4.dp, 4.dp),
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, ZashiColors.Surfaces.bgPrimary)
                                ) {
                                    Image(
                                        modifier = Modifier.size(14.dp),
                                        painter = painterResource(state.smallIcon.resource),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            SelectionContainer {
                ZashiAutoSizeText(
                    textAlign = TextAlign.Center,
                    text = state.subtitle.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Medium,
                    color = ZashiColors.Text.textTertiary,
                    maxLines = 1,
                )
            }
        }
    }
}

@Immutable
data class SwapTokenAmountState(
    val bigIcon: ImageResource?,
    val smallIcon: ImageResource?,
    val title: StringResource,
    val subtitle: StringResource
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            SwapQuoteAmount(
                modifier = Modifier.fillMaxWidth(.75f),
                state =
                    SwapTokenAmountState(
                        bigIcon = imageRes(R.drawable.ic_zec_round_full),
                        smallIcon = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_receive_shield),
                        title = stringResByDynamicCurrencyNumber(0.000000421423154, "", TickerLocation.HIDDEN),
                        subtitle = stringResByDynamicCurrencyNumber(0.0000000000000021312, "$")
                    )
            )
        }
    }
