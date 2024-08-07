@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ZashiColors(
    val textLight: Color,
    val textLightSupport: Color,
    val surfacePrimary: Color,
)

internal val LightZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFFFFFFF),
        textLightSupport = Color(0xFFD9D8CF),
        surfacePrimary = Color(0xFF282622),
    )

internal val DarkZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFE8E8E8),
        textLightSupport = Color(0xFFBDBBBC),
        surfacePrimary = Color(0xFF454243),
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiColors = staticCompositionLocalOf<ZashiColors> { error("no colors specified") }
