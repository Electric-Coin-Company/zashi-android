@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import co.electriccoin.zcash.ui.design.theme.internal.DarkExchangeRateColorPalette
import co.electriccoin.zcash.ui.design.theme.internal.ExchangeRateColors
import co.electriccoin.zcash.ui.design.theme.internal.LightExchangeRateColorPalette

// TODO [#1555]: extract colors to separate file
// TODO [#1555]: https://github.com/Electric-Coin-Company/zashi-android/issues/1555
@Immutable
data class ZashiColors(
    val textLight: Color,
    val textLightSupport: Color,
    val surfacePrimary: Color,
    val bgPrimary: Color,
    val bgSecondary: Color,
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
    val exchangeRateColors: ExchangeRateColors,
    val textSupport: Color,
    val stroke: Color,
    val divider: Color,
    val utilitySuccess700: Color,
    val utilitySuccess200: Color,
    val utilitySuccess50: Color,
    val btnDestroyFg: Color,
    val btnDestroyBg: Color,
    val btnDestroyBorder: Color,
)

internal val LightZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFFFFFFF),
        textLightSupport = Color(0xFFD9D8CF),
        surfacePrimary = Color(0xFF282622),
        bgPrimary = Color(0xFFFFFFFF),
        bgSecondary = Color(0xFFF4F4F4),
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
        btnTextFg = Color(0xFF231F20),
        exchangeRateColors = LightExchangeRateColorPalette,
        textSupport = Color(0xFF94907B),
        stroke = Color(0xFFD9D8CF),
        divider = Color(0xFFF7F7F5),
        utilitySuccess700 = Color(0xFF098605),
        utilitySuccess200 = Color(0xFFA3FF95),
        utilitySuccess50 = Color(0xFFEAFFE5),
        btnDestroyFg = Color(0xFFD92D20),
        btnDestroyBg = Color(0xFFFFFFFF),
        btnDestroyBorder = Color(0xFFFDA29B)
    )

internal val DarkZashiColorPalette =
    ZashiColors(
        textLight = Color(0xFFE8E8E8),
        textLightSupport = Color(0xFFBDBBBC),
        surfacePrimary = Color(0xFF454243),
        bgPrimary = Color(0xFF231F20),
        bgSecondary = Color(0xFF3B3839),
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
        btnTextFg = Color(0xFFE8E8E8),
        exchangeRateColors = DarkExchangeRateColorPalette,
        textSupport = Color(0xFF7E7C7C),
        stroke = Color(0xFFA7A5A6),
        divider = Color(0xFF343031),
        utilitySuccess700 = Color(0xFF098605),
        utilitySuccess200 = Color(0xFFA3FF95),
        utilitySuccess50 = Color(0xFFEAFFE5),
        btnDestroyFg = Color(0xFFFEE4E2),
        btnDestroyBg = Color(0xFF55160C),
        btnDestroyBorder = Color(0xFF912018)
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiColors = staticCompositionLocalOf<ZashiColors> { error("no colors specified") }
