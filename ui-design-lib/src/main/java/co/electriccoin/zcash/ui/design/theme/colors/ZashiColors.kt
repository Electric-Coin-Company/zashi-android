@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.colors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val ZashiColors: ZashiColorsInternal
    @Composable get() = LocalZashiColors.current

val ZashiLightColors: ZashiColorsInternal
    @Composable get() = LocalLightZashiColors.current

val ZashiDarkColors: ZashiColorsInternal
    @Composable get() = LocalDarkZashiColors.current

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiColors = staticCompositionLocalOf<ZashiColorsInternal> { error("no colors specified") }

@Suppress("CompositionLocalAllowlist")
internal val LocalLightZashiColors = staticCompositionLocalOf { LightZashiColorsInternal }

@Suppress("CompositionLocalAllowlist")
internal val LocalDarkZashiColors = staticCompositionLocalOf { DarkZashiColorsInternal }
