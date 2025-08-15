package co.electriccoin.zcash.ui.screen.transactiondetail.infoitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun TransactionDetailInfoContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface (
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ZashiColors.Surfaces.bgSecondary,
    ) {
        Column {
            content()
        }
    }
}
