package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoShape.FIRST

@Composable
fun TransactionDetailInfoRow(
    state: TransactionDetailInfoRowState,
    modifier: Modifier = Modifier
) {
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
        shape = state.shape.shape,
        color = ZashiColors.Surfaces.bgSecondary,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    text = state.title.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary
                )
                state.message?.let {
                    Spacer(Modifier.width(16.dp))
                    SelectionContainer {
                        Text(
                            maxLines = 1,
                            modifier = Modifier,
                            text = it.getValue(),
                            style = ZashiTypography.textSm,
                            color = ZashiColors.Text.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                state.trailingIcon?.let {
                    Spacer(Modifier.width(6.dp))
                    Image(
                        painter = painterResource(it),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

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
