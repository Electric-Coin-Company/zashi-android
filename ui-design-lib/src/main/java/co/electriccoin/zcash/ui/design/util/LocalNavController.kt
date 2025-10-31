package co.electriccoin.zcash.ui.design.util

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

@Suppress("CompositionLocalAllowlist")
val LocalNavController =
    compositionLocalOf<NavHostController> {
        error("NavController not provided")
    }
