@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.dimensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val ZashiDimensions: ZashiDimensionsInternal
    @Composable get() = LocalZashiDimensions.current

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiDimensions = staticCompositionLocalOf<ZashiDimensionsInternal> { error("no colors specified") }
