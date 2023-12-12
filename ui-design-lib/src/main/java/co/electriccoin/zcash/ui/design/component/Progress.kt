package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun CircularScreenProgressIndicatorComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            CircularScreenProgressIndicator()
        }
    }
}

@Composable
fun CircularScreenProgressIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(ZcashTheme.dimens.circularScreenProgressWidth)
        )
    }
}