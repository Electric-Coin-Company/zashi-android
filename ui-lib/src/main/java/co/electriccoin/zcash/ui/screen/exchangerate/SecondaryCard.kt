package co.electriccoin.zcash.ui.screen.exchangerate

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
internal fun SecondaryCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        border = BorderStroke(1.dp, ZashiColors.Surfaces.strokeSecondary),
        colors =
            CardDefaults.cardColors(
                containerColor = ZashiColors.Surfaces.bgPrimary
            ),
        content = content
    )
}
