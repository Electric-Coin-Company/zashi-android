package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun ZashiCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ZashiColors.Surfaces.bgSecondary,
            contentColor = ZashiColors.Text.textTertiary
        ),
    ) {
        Column(
            Modifier.padding(24.dp)
        ) {
            content()
        }
    }
}
