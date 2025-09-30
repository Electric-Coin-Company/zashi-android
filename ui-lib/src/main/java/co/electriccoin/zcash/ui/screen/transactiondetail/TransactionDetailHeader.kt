package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiTextOrShimmer
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.Compose
import co.electriccoin.zcash.ui.design.util.ComposeAsShimmerCircle
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.TickerLocation.HIDDEN
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.orHiddenString
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("MagicNumber")
@Composable
fun TransactionDetailHeader(
    state: TransactionDetailHeaderState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.width(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val iconSize = 48.dp
            val iconHorizontalOffset = .1f
            val iconCutoutWidth = .75f
            val iconCutoutOffset = 1f + iconHorizontalOffset + (1 - iconCutoutWidth)
            if (state.icons.size > 1) {
                Spacer((iconSize * iconHorizontalOffset) * (state.icons.size - 1))
            }

            state.icons.forEachIndexed { index, icon ->

                val cutout =
                    if (state.icons.size <= 1 || state.icons.lastIndex == index) {
                        Modifier
                    } else {
                        Modifier
                            .graphicsLayer {
                                compositingStrategy = CompositingStrategy.Offscreen
                            }.drawWithContent {
                                drawContent()
                                drawCircle(
                                    color = Color(0xFFFFFFFF),
                                    radius = size.width / 2f,
                                    center = Offset(x = size.width * iconCutoutOffset, y = size.height / 2f),
                                    blendMode = BlendMode.DstOut
                                )
                            }.background(Color.Transparent)
                    }

                val offset =
                    if (index == 0) {
                        Modifier
                    } else {
                        Modifier.offset(x = -(iconSize * iconHorizontalOffset) * index)
                    }

                val iconModifier = offset then cutout

                when (icon) {
                    is ImageResource.ByDrawable ->
                        icon.Compose(
                            modifier =
                                iconModifier then
                                    if (state.icons.size > 1) {
                                        Modifier.size(iconSize)
                                    } else {
                                        Modifier
                                    },
                        )

                    is ImageResource.Loading ->
                        icon.ComposeAsShimmerCircle(
                            modifier = iconModifier,
                            size = iconSize
                        )

                    is ImageResource.DisplayString -> {
                        // do nothing
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        ZashiTextOrShimmer(
            text = state.title?.getValue(),
            shimmerWidth = 120.dp,
            style = ZashiTypography.textLg,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textTertiary
        )
        Spacer(Modifier.height(2.dp))
        SelectionContainer {
            Row {
                ZashiTextOrShimmer(
                    text =
                        if (state.amount == null) {
                            null
                        } else {
                            state.amount orHiddenString
                                stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder)
                        },
                    shimmerWidth = 178.dp,
                    style = ZashiTypography.header3,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                if (state.amount != null && LocalBalancesAvailable.current) {
                    Text(
                        text = stringResource(cash.z.ecc.sdk.ext.R.string.zcash_token_zec),
                        style = ZashiTypography.header3,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textQuaternary
                    )
                }
            }
        }
    }
}

@Immutable
data class TransactionDetailHeaderState(
    val title: StringResource?,
    val amount: StringResource?,
    val icons: List<ImageResource>
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailHeader(
                state =
                    TransactionDetailHeaderState(
                        title = stringRes("Sending"),
                        amount = stringRes(Zatoshi(100000000), HIDDEN),
                        icons =
                            listOf(
                                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
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
            TransactionDetailHeader(
                state =
                    TransactionDetailHeaderState(
                        title = null,
                        amount = null,
                        icons =
                            listOf(
                                loadingImageRes(),
                                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_chain_zec),
                            )
                    )
            )
        }
    }
