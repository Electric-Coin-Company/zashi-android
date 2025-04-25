package co.electriccoin.zcash.ui.design.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import co.electriccoin.zcash.spackle.AndroidApiVersion

fun Modifier.blurCompat(
    radius: Dp,
    max: Dp
): Modifier =
    if (AndroidApiVersion.isAtLeastS) {
        this.blur(radius)
    } else {
        val progression = 1 - (radius.value / max.value)
        this
            .alpha(progression)
    }
