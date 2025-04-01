package co.electriccoin.zcash.ui.screen.home.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeMessageWrapper(
    color: Color,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = color,
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}