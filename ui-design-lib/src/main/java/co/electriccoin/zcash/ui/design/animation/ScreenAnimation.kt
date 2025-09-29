package co.electriccoin.zcash.ui.design.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

object ScreenAnimation {
    private const val DURATION = 400

    fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            initialOffset = { it },
            animationSpec = tween()
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            targetOffset = { it / 2 },
            animationSpec = tween()
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition() =
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            initialOffset = { it / 2 },
            animationSpec = tween()
        )

    fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition() =
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            targetOffset = { it },
            animationSpec = tween()
        )

    private fun <T> tween(): TweenSpec<T> =
        tween(
            durationMillis = DURATION
        )
}
