@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val ZashiColors: ZashiColorsInternal
    @Composable get() = LocalZashiColors.current

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiColors = staticCompositionLocalOf<ZashiColorsInternal> { error("no colors specified") }
