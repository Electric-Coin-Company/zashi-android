package co.electriccoin.zcash.ui.screen.transactiondetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.balances.LocalBalancesAvailable
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orHiddenString
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun TransactionDetailHeader(
    iconState: TransactionDetailIconHeaderState,
    state: TransactionDetailHeaderState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(
                modifier =
                    Modifier.width(
                        when (iconState.icons.size) {
                            2 -> 10.dp
                            else -> 22.dp
                        }
                    )
            )
            iconState.icons.forEachIndexed { index, icon ->
                Image(
                    modifier =
                        when (index) {
                            0 -> Modifier
                            1 -> Modifier.offset(x = (-10).dp)
                            else -> Modifier.offset(x = (-22).dp)
                        },
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = state.title.getValue(),
            style = ZashiTypography.textLg,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textTertiary
        )
        Spacer(Modifier.height(2.dp))
        SelectionContainer {
            Row {
                Text(
                    text =
                        state.amount orHiddenString
                            stringRes(co.electriccoin.zcash.ui.design.R.string.hide_balance_placeholder),
                    style = ZashiTypography.header3,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                if (LocalBalancesAvailable.current) {
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

data class TransactionDetailHeaderState(
    val title: StringResource,
    val amount: StringResource,
)

data class TransactionDetailIconHeaderState(
    val icons: List<Int>
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailHeader(
                iconState =
                    TransactionDetailIconHeaderState(
                        listOf(
                            R.drawable.ic_transaction_detail_z,
                            R.drawable.ic_transaction_detail_private,
                            R.drawable.ic_transaction_detail_shield
                        )
                    ),
                state =
                    TransactionDetailHeaderState(
                        title = stringRes("Sending"),
                        amount = stringRes(Zatoshi(100000000))
                    )
            )
        }
    }
