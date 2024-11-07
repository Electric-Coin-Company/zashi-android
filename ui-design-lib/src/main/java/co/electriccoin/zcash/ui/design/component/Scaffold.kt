package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Scaffold with blank background")
@Composable
private fun BlankBgScaffoldComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankBgScaffold {
            Text(text = "Blank background scaffold")
        }
    }
}

@Composable
fun BlankBgScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = ZcashTheme.colors.backgroundColor,
        topBar = topBar,
        snackbarHost = snackbarHost,
        bottomBar = bottomBar,
        content = content,
        modifier = modifier,
    )
}

@Composable
fun GradientBgScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    startColor: Color,
    endColor: Color,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = topBar,
        snackbarHost = snackbarHost,
        bottomBar = bottomBar,
        content = content,
        modifier = modifier
            .background(zashiVerticalGradient(startColor, endColor)),
    )
}
