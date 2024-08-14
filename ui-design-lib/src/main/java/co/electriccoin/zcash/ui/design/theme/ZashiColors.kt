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
    val bgPrimary: Color,
    val defaultFg: Color,
    val textTertiary: Color,
    val textPrimary: Color,
    val strokeSecondary: Color,
    val btnTertiaryBg: Color,
    val btnTertiaryFg: Color,
    val btnPrimaryBg: Color,
    val btnPrimaryBgDisabled: Color,
    val btnPrimaryFg: Color,
    val btnPrimaryFgDisabled: Color,
    val btnTextFg: Color,
)

internal val LightZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFFFFFFF),
        textLightSupport = Color(0xFFD9D8CF),
        surfacePrimary = Color(0xFF282622),
        bgPrimary = Color(0xFFFFFFFF),
        defaultFg = Color(0xFFD9D8CF),
        textPrimary = Color(0xFF231F20),
        textTertiary = Color(0xFF716C5D),
        strokeSecondary = Color(0xFFEBEBE6),
        btnTertiaryBg = Color(0xFFEBEBE6),
        btnTertiaryFg = Color(0xFF4D4941),
        btnPrimaryBg = Color(0xFF231F20),
        btnPrimaryBgDisabled = Color(0xFFEBEBE6),
        btnPrimaryFg = Color(0xFFFFFFFF),
        btnPrimaryFgDisabled = Color(0xFF94907B),
        btnTextFg = Color(0xFF231F20)
    )

internal val DarkZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFE8E8E8),
        textLightSupport = Color(0xFFBDBBBC),
        surfacePrimary = Color(0xFF454243),
        bgPrimary = Color(0xFF231F20),
        defaultFg = Color(0xFFBDBBBC),
        textPrimary = Color(0xFFE8E8E8),
        textTertiary = Color(0xFFBDBBBC),
        strokeSecondary = Color(0xFF454243),
        btnTertiaryBg = Color(0xFF343031),
        btnTertiaryFg = Color(0xFFD2D1D2),
        btnPrimaryBg = Color(0xFFFFFFFF),
        btnPrimaryBgDisabled = Color(0xFF343031),
        btnPrimaryFg = Color(0xFF231F20),
        btnPrimaryFgDisabled = Color(0xFF7E7C7C),
        btnTextFg = Color(0xFFE8E8E8)
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiColors = staticCompositionLocalOf<ZashiColors> { error("no colors specified") }
