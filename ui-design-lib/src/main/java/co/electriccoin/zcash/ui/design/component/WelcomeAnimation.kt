@file:Suppress("MatchingDeclarationName")

package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.AnimationConstants.ANIMATION_DURATION
import co.electriccoin.zcash.ui.design.component.AnimationConstants.INITIAL_DELAY
import co.electriccoin.zcash.ui.design.component.AnimationConstants.WELCOME_ANIM_TEST_TAG
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.screenHeight
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object AnimationConstants {
    const val ANIMATION_DURATION = 700
    const val INITIAL_DELAY = 1000
    const val WELCOME_ANIM_TEST_TAG = "WELCOME_ANIM_TEST_TAG"

    fun together() = (ANIMATION_DURATION + INITIAL_DELAY).toLong()
}

// TODO [#1002]: Welcome screen animation masking
// TODO [#1002]: https://github.com/Electric-Coin-Company/zashi-android/issues/1002

@Composable
fun WelcomeAnimationAutostart(
    modifier: Modifier = Modifier,
    delay: Duration = INITIAL_DELAY.milliseconds,
) {
    var currentAnimationState by remember { mutableStateOf(true) }

    WelcomeAnimation(
        animationState = currentAnimationState,
        modifier = modifier.testTag(WELCOME_ANIM_TEST_TAG)
    )

    // Let's start the animation automatically in case e.g. authentication is not involved
    LaunchedEffect(key1 = currentAnimationState) {
        delay(delay)
        currentAnimationState = false
    }
}

private const val LOGO_RELATIVE_LOCATION = 0.2f

@Composable
@Suppress("LongMethod")
fun WelcomeAnimation(
    animationState: Boolean,
    modifier: Modifier = Modifier,
) {
    val screenHeight = screenHeight()

    Column(
        modifier =
            modifier.then(
                Modifier
                    .verticalScroll(
                        state = rememberScrollState(),
                        enabled = false
                    )
                    .wrapContentSize()
            )
    ) {
        AnimatedVisibility(
            visible = animationState,
            exit =
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec =
                        tween(
                            durationMillis = ANIMATION_DURATION,
                            easing = FastOutLinearInEasing
                        )
                ),
        ) {
            Box(modifier = Modifier.wrapContentSize()) {
                Column(modifier = Modifier.wrapContentSize()) {
                    Image(
                        painter = ColorPainter(ZcashTheme.colors.welcomeAnimationColor),
                        contentScale = ContentScale.FillBounds,
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .height(screenHeight.overallScreenHeight()),
                        contentDescription = null
                    )
                    Image(
                        painter = painterResource(id = R.drawable.chart_line),
                        contentScale = ContentScale.FillBounds,
                        colorFilter = ColorFilter.tint(color = ZcashTheme.colors.welcomeAnimationColor),
                        contentDescription = null,
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .height(screenHeight.overallScreenHeight()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.fillMaxHeight(LOGO_RELATIVE_LOCATION))

                    Image(
                        painter = painterResource(id = R.drawable.logo_with_hi),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
