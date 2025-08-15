package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.valentinilk.shimmer.shimmer

@Composable
fun TransactionDetailInfoRow(
    state: TransactionDetailInfoRowState,
    modifier: Modifier = Modifier
) {
    TransactionDetailRowSurface(
        onClick = state.onClick,
        modifier = modifier,
    ) {
        Text(
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
                modifier = Modifier
                    .weight(1f)
                    .shimmer(rememberZashiShimmer()),
                contentAlignment = Alignment.CenterEnd
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
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Box (
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
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Immutable
data class TransactionDetailInfoRowState(
    val title: StringResource,
    val message: StringResource? = null,
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
                        onClick = {}
                    )
            )
        }
    }

