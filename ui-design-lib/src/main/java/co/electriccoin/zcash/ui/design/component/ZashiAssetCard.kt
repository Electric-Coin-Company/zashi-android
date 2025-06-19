package co.electriccoin.zcash.ui.design.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
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
fun ZashiAssetCard(
    state: AssetCardState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = state.onClick
    ) {
        Content(state)
    }
}

@Composable
private fun Content(state: AssetCardState) {
    Row(
        modifier =
            Modifier.padding(
                start = if (state.bigIcon is ImageResource.ByDrawable) 4.dp else 14.dp,
                top = if (state.bigIcon is ImageResource.ByDrawable) 4.dp else 8.dp,
                end = 12.dp,
                bottom = if (state.bigIcon is ImageResource.ByDrawable) 4.dp else 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.bigIcon is ImageResource.ByDrawable) {
            Box {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(state.bigIcon.resource),
                    contentDescription = null
                )

                if (state.smallIcon is ImageResource.ByDrawable) {
                    Image(
                        modifier =
                            Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .offset(4.dp, 4.dp),
                        painter = painterResource(state.smallIcon.resource),
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
        Spacer(4.dp)
        if (state.onClick != null) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_down_small),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Card(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    if (onClick == null) {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = ZashiColors.Surfaces.bgPrimary,
            border = BorderStroke(.33.dp, Color.Transparent),
            shadowElevation = 0.dp,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = ZashiColors.Surfaces.bgPrimary,
            border = BorderStroke(.33.dp, ZashiColors.Surfaces.strokeSecondary),
            shadowElevation = 1.dp,
            content = content,
            onClick = onClick
        )
    }
}

@Immutable
data class AssetCardState(
    val ticker: StringResource,
    val bigIcon: ImageResource?,
    val smallIcon: ImageResource?,
    val onClick: (() -> Unit)?
)

@PreviewScreens
@Composable
private fun ClickablePreview() =
    ZcashTheme {
        BlankSurface {
            ZashiAssetCard(
                state =
                    AssetCardState(
                        ticker = stringRes("USDT"),
                        bigIcon = imageRes(R.drawable.ic_token_zec),
                        smallIcon = imageRes(R.drawable.ic_chain_zec),
                        onClick = {}
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun UnclickablePreview() =
    ZcashTheme {
        BlankSurface {
            ZashiAssetCard(
                state =
                    AssetCardState(
                        ticker = stringRes("USDT"),
                        bigIcon = imageRes(R.drawable.ic_token_zec),
                        smallIcon = imageRes(R.drawable.ic_chain_zec),
                        onClick = null
                    )
            )
        }
    }
