package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieProgress(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (isSystemInDarkTheme()) R.raw.lottie_loading_white else R.raw.lottie_loading
        )
    )
    val progress by animateLottieCompositionAsState(
        iterations = LottieConstants.IterateForever,
        composition = composition
    )
    LottieAnimation(
        modifier = modifier.size(16.dp),
        composition = composition,
        progress = { progress },
        maintainOriginalImageBounds = true
    )
}
