package co.electriccoin.zcash.ui.design.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

object ScreenAnimation {
    private const val DURATION = 400

    fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            initialOffset = { it },
            animationSpec = tween(duration = DURATION.milliseconds)
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            targetOffset = { it },
            animationSpec = tween(duration = DURATION.milliseconds)
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            initialOffset = { it },
            animationSpec = tween(duration = DURATION.milliseconds)
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            targetOffset = { it },
            animationSpec = tween(duration = DURATION.milliseconds)
        )
}

private fun tween(duration: Duration): TweenSpec<IntOffset> =
    tween(
        durationMillis = duration.toInt(DurationUnit.MILLISECONDS)
    )
