package co.electriccoin.zcash.ui.screen.transactionhistory

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orHiddenString
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun Transaction(
    state: TransactionState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = ripple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state.onClick,
                    role = Role.Button,
                )
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(state.icon),
                contentDescription = null
            )
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    Text(
                        text = state.title.getValue(),
                        color = ZashiColors.Text.textPrimary,
                        style = ZashiTypography.textSm,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (state.isShielded) {
                        Spacer(Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_transaction_private),
                            contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                state.subtitle?.let {
                    Text(
                        text = it.getValue(),
                        color = ZashiColors.Text.textTertiary,
                        style = ZashiTypography.textSm,
                    )
                }
            }
            state.value?.let {
                Spacer(Modifier.width(16.dp))
                Text(
                    text =
                    it orHiddenString stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder),
                    color = ZashiColors.Text.textPrimary,
                    style = ZashiTypography.textSm
                )
            }
        }
    }
}

data class TransactionState(
    @DrawableRes val icon: Int,
    val title: StringResource,
    val subtitle: StringResource?,
    val isShielded: Boolean,
    val value: StringResource?,
    val onClick: () -> Unit,
)

@PreviewScreens
@Composable
private fun TransactionPreview() = ZcashTheme {
    BlankSurface {
        Transaction(
            state = TransactionState(
                icon = R.drawable.ic_transaction_sent,
                title = stringRes("Transaction Title"),
                subtitle = stringRes("Transaction subtitle"),
                isShielded = true,
                value = stringRes(R.string.transaction_history_plus, stringRes(Zatoshi(10000000))),
                onClick = {}
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}