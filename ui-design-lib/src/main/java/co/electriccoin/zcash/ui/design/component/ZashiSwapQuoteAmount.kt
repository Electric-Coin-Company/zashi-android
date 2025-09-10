package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.orHiddenString
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import com.valentinilk.shimmer.shimmer
import java.math.BigDecimal

@Composable
internal fun ZashiSwapQuoteAmount(
    state: SwapTokenAmountState?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ZashiColors.Surfaces.bgSecondary,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (state == null) Loading() else Data(state = state)
    }
}

@Composable
private fun Loading() {
    Column(
        modifier =
            Modifier
                .padding(12.dp)
                .shimmer(rememberZashiShimmer()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            ShimmerCircle(
                size = 20.dp,
                color = ZashiColors.Surfaces.bgTertiary
            )
            Box(
                modifier =
                    Modifier
                        .offset(2.dp, 2.dp)
                        .size(12.dp)
                        .border(1.dp, ZashiColors.Surfaces.bgSecondary, CircleShape)
                        .align(Alignment.BottomEnd)
                        .background(ZashiColors.Surfaces.bgTertiary, CircleShape)
            )
        }
        Spacer(4.dp)
        val titleTextSize =
            measureTextStyle(
                text = stringResByNumber(BigDecimal(".123456")).getValue(),
                style =
                    ZashiTypography.textXl.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
            )
        ShimmerRectangle(
            width = titleTextSize.size.widthDp,
            height = titleTextSize.size.heightDp,
            color = ZashiColors.Surfaces.bgTertiary
        )
        Spacer(4.dp)
        val subtitleTextSize =
            measureTextStyle(
                text = stringResByNumber(BigDecimal(".123")).getValue(),
                style =
                    ZashiTypography.textSm.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
            )
        ShimmerRectangle(
            width = subtitleTextSize.size.widthDp,
            height = subtitleTextSize.size.heightDp,
            color = ZashiColors.Surfaces.bgTertiary
        )
    }
}

@Composable
private fun Data(state: SwapTokenAmountState) {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.bigIcon is ImageResource.ByDrawable) {
            Box {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(state.bigIcon.resource),
                    contentDescription = null
                )

                if (state.smallIcon is ImageResource.ByDrawable) {
                    if (state.smallIcon.resource ==
                        R.drawable.ic_receive_shield
                    ) {
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
                            modifier =
                                Modifier
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
            Spacer(4.dp)
        }
        SelectionContainer {
            ZashiAutoSizeText(
                modifier = Modifier.weight(1f, false),
                textAlign = TextAlign.Center,
                text = state.title orHiddenString stringRes(R.string.hide_balance_placeholder),
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold,
                color = ZashiColors.Text.textPrimary,
                maxLines = 1,
            )
        }
        Spacer(4.dp)
        SelectionContainer {
            ZashiAutoSizeText(
                textAlign = TextAlign.Center,
                text = state.subtitle orHiddenString stringRes(R.string.hide_balance_placeholder),
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textTertiary,
                maxLines = 1,
            )
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
            ZashiSwapQuoteAmount(
                state =
                    SwapTokenAmountState(
                        bigIcon = imageRes(R.drawable.ic_chain_placeholder),
                        smallIcon = imageRes(R.drawable.ic_receive_shield),
                        title = stringResByDynamicCurrencyNumber(0.000000421423154, "", TickerLocation.HIDDEN),
                        subtitle = stringResByDynamicCurrencyNumber(0.0000000000000021312, "$")
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiSwapQuoteAmount(
                state = null
            )
        }
    }
