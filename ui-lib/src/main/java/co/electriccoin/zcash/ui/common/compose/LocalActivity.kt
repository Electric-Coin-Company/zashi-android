package co.electriccoin.zcash.ui.common.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.compositionLocalOf

@Suppress("CompositionLocalAllowlist")
val LocalActivity = compositionLocalOf<ComponentActivity> { error("Activity not provided") }
