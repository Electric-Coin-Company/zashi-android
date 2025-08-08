package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.MIDDLE

@Composable
fun TransactionDetailInfoColumn(
    state: TransactionDetailInfoColumnState,
    modifier: Modifier = Modifier
) {
    val topStart by animateDpAsState(state.shape.topStart)
    val topEnd by animateDpAsState(state.shape.topEnd)
    val bottomStart by animateDpAsState(state.shape.bottomStart)
    val bottomEnd by animateDpAsState(state.shape.bottomEnd)
    Surface(
        modifier =
            modifier then
                if (state.onClick != null) {
                    Modifier.clickable(
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = state.onClick,
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
        Column(
            modifier =
                Modifier.padding(
                    top = if (state.title != null) 14.dp else 0.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 14.dp
                )
        ) {
            state.title?.let {
                Text(
                    text = it.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary,
                    maxLines = 1,
                )
                Spacer(Modifier.width(4.dp))
            }

            SelectionContainer {
                Text(
                    text = state.message.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class TransactionDetailInfoColumnState(
    val title: StringResource?,
    val message: StringResource,
    val shape: TransactionDetailInfoShape,
    val onClick: (() -> Unit)?,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailInfoColumn(
                modifier = Modifier.fillMaxWidth(),
                state =
                    TransactionDetailInfoColumnState(
                        title = stringRes("Title"),
                        message = stringRes("Message"),
                        shape = MIDDLE,
                        onClick = {}
                    )
            )
        }
    }

@PreviewScreens
@Composable
private fun NoTitlePreview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailInfoColumn(
                modifier = Modifier.fillMaxWidth(),
                state =
                    TransactionDetailInfoColumnState(
                        title = null,
                        message = stringRes("Message"),
                        shape = MIDDLE,
                        onClick = {}
                    )
            )
        }
    }
