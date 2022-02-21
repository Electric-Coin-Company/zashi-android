package cash.z.ecc.ui.design.component

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cash.z.ecc.ui.theme.ZcashTheme
import co.electriccoin.zcash.spackle.model.Progress

// Eventually rename to GradientLinearProgressIndicator
@Composable
fun PinkProgress(progress: Progress, modifier: Modifier = Modifier) {
    // Needs custom implementation to apply gradient
    LinearProgressIndicator(
        progress = progress.percent(), modifier,
        ZcashTheme.colors.progressStart, ZcashTheme.colors.progressBackground
    )
}

private fun Progress.percent() = (current.value + 1.toFloat()) / (last.value + 1).toFloat()
