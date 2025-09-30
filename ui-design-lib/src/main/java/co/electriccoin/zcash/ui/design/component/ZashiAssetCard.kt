package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
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
import com.valentinilk.shimmer.shimmer

@Composable
fun ZashiAssetCard(state: AssetCardState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        onClick = state.onClick.takeIf { state.isEnabled }
    ) {
        Content(state)
    }
}

@Composable
private fun Content(state: AssetCardState) {
    val onClick = state.onClick
    val clickModifier =
        if (onClick != null) {
            Modifier.clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
        } else {
            Modifier
        }
    when (state) {
        is AssetCardState.Data -> Data(state, clickModifier)
        is AssetCardState.Loading -> Loading(state, clickModifier)
    }
}

@Composable
private fun Data(state: AssetCardState.Data, modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier then
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
                    if (state.smallIcon.resource in
                        listOf(R.drawable.ic_receive_shield, R.drawable.ic_zec_unshielded)
                    ) {
                        Image(
                            modifier =
                                Modifier
                                    .size(14.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(4.dp, 4.dp),
                            painter = painterResource(state.smallIcon.resource),
                            contentDescription = null,
                        )
                    } else {
                        Surface(
                            modifier =
                                Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(6.dp, 2.dp),
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
            Spacer(8.dp)
        }
        ZashiAutoSizeText(
            text = state.ticker.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Spacer(4.dp)
        if (state.onClick != null && state.isEnabled) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_down_small),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Loading(
    state: AssetCardState.Loading,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier then
                Modifier.padding(
                    start = 4.dp,
                    top = 4.dp,
                    end = 12.dp,
                    bottom = 4.dp,
                ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.shimmer(customShimmer = rememberZashiShimmer()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerCircle()
            Spacer(4.dp)
            ShimmerRectangle()
        }
        Spacer(4.dp)
        if (state.onClick != null && state.isEnabled) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_down_small),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Card(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
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
        )
    }
}

@Immutable
sealed interface AssetCardState {
    val isEnabled: Boolean
    val onClick: (() -> Unit)?

    @Immutable
    data class Data(
        val ticker: StringResource,
        val bigIcon: ImageResource?,
        val smallIcon: ImageResource?,
        override val isEnabled: Boolean = true,
        override val onClick: (() -> Unit)?,
    ) : AssetCardState

    @Immutable
    data class Loading(
        override val isEnabled: Boolean = true,
        override val onClick: (() -> Unit)?,
    ) : AssetCardState
}

@PreviewScreens
@Composable
private fun ClickablePreview() =
    ZcashTheme {
        BlankSurface {
            ZashiAssetCard(
                state =
                    AssetCardState.Data(
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
                    AssetCardState.Data(
                        ticker = stringRes("USDT"),
                        bigIcon = imageRes(R.drawable.ic_token_zec),
                        smallIcon = imageRes(R.drawable.ic_chain_zec),
                        onClick = null
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiAssetCard(
                state =
                    AssetCardState.Loading(
                        onClick = {}
                    )
            )
        }
    }
