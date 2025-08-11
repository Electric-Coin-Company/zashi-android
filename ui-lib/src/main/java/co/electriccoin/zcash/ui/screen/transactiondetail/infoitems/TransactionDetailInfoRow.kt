package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ShimmerRectangle
import co.electriccoin.zcash.ui.design.component.rememberZashiShimmer
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.FIRST
import com.valentinilk.shimmer.shimmer

@Composable
fun TransactionDetailInfoRow(
    state: TransactionDetailInfoRowState,
    modifier: Modifier = Modifier
) {
    TransactionDetailRowSurface(
        onClick = state.onClick,
        shape = state.shape,
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.width(IntrinsicSize.Min),
            maxLines = 1,
            text = state.title.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary
        )
        if (state.message != null) {
            Spacer(Modifier.width(16.dp))
            SelectionContainer(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    text = state.message.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textPrimary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End
                )
            }
        } else {
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier.shimmer(rememberZashiShimmer())
            ) {
                ShimmerRectangle(
                    width = 64.dp,
                    height = 20.dp,
                    color = ZashiColors.Surfaces.bgTertiary
                )
            }
        }
        if (state.trailingIcon != null && state.message != null) {
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(state.trailingIcon),
                contentDescription = null
            )
        }
    }
}

@Composable
fun TransactionDetailRowSurface(
    onClick: (() -> Unit)?,
    shape: TransactionDetailInfoShape,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val topStart by animateDpAsState(
        targetValue = shape.topStart,
        animationSpec = shapeAnimation(shape.topStart)
    )
    val topEnd by animateDpAsState(
        targetValue = shape.topEnd,
        animationSpec = shapeAnimation(shape.topEnd)
    )
    val bottomStart by animateDpAsState(
        targetValue = shape.bottomStart,
        animationSpec = shapeAnimation(shape.bottomStart)
    )
    val bottomEnd by animateDpAsState(
        targetValue = shape.bottomEnd,
        animationSpec = shapeAnimation(shape.bottomEnd)
    )

    Surface(
        modifier =
            modifier then
                if (onClick != null) {
                    Modifier.clickable(
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                        role = Role.Button,
                    )
                } else {
                    Modifier
                },
        shape = RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomStart = bottomStart,
            bottomEnd = bottomEnd
        ),
        color = ZashiColors.Surfaces.bgSecondary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun shapeAnimation(target: Dp): AnimationSpec<Dp> =
    if (target > 0.dp) tween(durationMillis = 300, delayMillis = 200) else snap()

@Immutable
data class TransactionDetailInfoRowState(
    val title: StringResource,
    val message: StringResource? = null,
    val shape: TransactionDetailInfoShape,
    @DrawableRes val trailingIcon: Int? = null,
    val onClick: (() -> Unit)? = null,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailInfoRow(
                state =
                    TransactionDetailInfoRowState(
                        title = stringRes("Title"),
                        message = stringRes("Message"),
                        trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                        shape = FIRST,
                        onClick = {}
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun LoadingPreview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailInfoRow(
                state =
                    TransactionDetailInfoRowState(
                        title = stringRes("Title"),
                        message = null,
                        trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                        shape = FIRST,
                        onClick = {}
                    )
            )
        }
    }

