package co.electriccoin.zcash.ui.design.component

import androidx.annotation.RawRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieProgress(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    @RawRes loadingRes: Int = if (isSystemInDarkTheme()) R.raw.lottie_loading_white else R.raw.lottie_loading
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(loadingRes))
    val progress by animateLottieCompositionAsState(
        iterations = LottieConstants.IterateForever,
        composition = composition
    )
    LottieAnimation(
        modifier = modifier.size(size),
        composition = composition,
        progress = { progress },
        maintainOriginalImageBounds = true
    )
}
