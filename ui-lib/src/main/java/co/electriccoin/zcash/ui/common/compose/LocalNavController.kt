package co.electriccoin.zcash.ui.common.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

@Suppress("CompositionLocalAllowlist")
val LocalNavController =
    compositionLocalOf<NavHostController> {
        error("NavController not provided")
    }
