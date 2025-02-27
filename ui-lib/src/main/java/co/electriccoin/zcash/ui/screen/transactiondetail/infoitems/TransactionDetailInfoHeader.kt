package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun TransactionDetailInfoHeader(
    state: TransactionDetailInfoHeaderState,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = state.title.getValue(),
        style = ZashiTypography.textSm,
        fontWeight = FontWeight.Medium,
        color = ZashiColors.Text.textTertiary
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            TransactionDetailInfoHeader(
                state =
                    TransactionDetailInfoHeaderState(
                        title = stringRes("Title")
                    )
            )
        }
    }

data class TransactionDetailInfoHeaderState(
    val title: StringResource
)
