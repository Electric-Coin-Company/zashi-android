@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExchangeRateColors(
    val btnSecondaryBg: Color,
    val btnSecondaryBorder: Color,
    val btnSecondaryFg: Color,
    val btnSpinnerDisabled: Color
)

internal val LightExchangeRateColorPalette =
    ExchangeRateColors(
        btnSecondaryBg = Color(0xFFFFFFFF),
        btnSecondaryBorder = Color(0xFFD9D8CF),
        btnSecondaryFg = Color(0xFF4D4941),
        btnSpinnerDisabled = Color(0x97989980)
    )

internal val DarkExchangeRateColorPalette =
    ExchangeRateColors(
        btnSecondaryBg = Color(0xFF4B4144),
        btnSecondaryBorder = Color(0xFF4B4144),
        btnSecondaryFg = Color(0xFFFFFFFF),
        btnSpinnerDisabled = Color(0xFF3D3A3B)
    )

@Suppress("CompositionLocalAllowlist")
internal val LocalExchangeRateColors = staticCompositionLocalOf<ExchangeRateColors> { error("no colors specified") }
