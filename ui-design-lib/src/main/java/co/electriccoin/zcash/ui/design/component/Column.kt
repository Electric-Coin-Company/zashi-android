package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Column with blank background")
@Composable
private fun BlankBgColumnComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankBgColumn {
            Text(text = "Blank background column")
        }
    }
}

@Preview("Column with grip pattern background")
@Composable
private fun GridBgScaffoldComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GridBgColumn {
            Text(text = "Grid pattern background column")
        }
    }
}

@Composable
fun BlankBgColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.then(Modifier.background(ZcashTheme.colors.backgroundColor)),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

@Composable
fun GridBgColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    GridSurface {
        Column(
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content,
            modifier = modifier,
        )
    }
}
