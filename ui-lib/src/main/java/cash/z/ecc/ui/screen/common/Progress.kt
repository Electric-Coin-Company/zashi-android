package cash.z.ecc.ui.screen.common

import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cash.z.ecc.ui.screen.onboarding.model.Progress
import cash.z.ecc.ui.theme.ZcashTheme

// Eventually rename to GradientLinearProgressIndicator
@Composable
fun PinkProgress(progress: Progress, modifier: Modifier = Modifier) {
    // Needs custom implementation to apply gradient
    LinearProgressIndicator(progress = progress.percent().decimal, modifier,
        ZcashTheme.colors.progressStart, ZcashTheme.colors.progressBackground)
}
