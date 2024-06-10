@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
interface ButtonColors {
    val containerColor: Color
    val disabledContainerColor: Color
    val shadowColor: Color
    val disabledShadowColor: Color
    val textColor: Color
    val disabledTextColor: Color
    val strokeColor: Color
    val disabledStrokeColor: Color
    val shadowStrokeColor: Color
    val shadowDisabledStrokeColor: Color
}

@Immutable
internal data class DefaultButtonColors(
    override val containerColor: Color = Color.Unspecified,
    override val disabledContainerColor: Color = Color.Unspecified,
    override val shadowColor: Color = Color.Unspecified,
    override val disabledShadowColor: Color = Color.Unspecified,
    override val textColor: Color = Color.Unspecified,
    override val disabledTextColor: Color = Color.Unspecified,
    override val strokeColor: Color = Color.Unspecified,
    override val disabledStrokeColor: Color = Color.Unspecified,
    override val shadowStrokeColor: Color = Color.Unspecified,
    override val shadowDisabledStrokeColor: Color = Color.Unspecified,
) : ButtonColors

// LIGHT THEME BUTTONS:

@Immutable
internal data class LightPrimaryButtonColors(
    override val containerColor: Color = Color(0xFF000000),
    override val disabledContainerColor: Color = Color(0xFFB7B7B7),
    override val shadowColor: Color = Color(0xFFFFFFFF),
    override val disabledShadowColor: Color = Color(0xFFFFFFFF),
    override val textColor: Color = Color(0xFFFFFFFF),
    override val disabledTextColor: Color = Color(0xFFDDDDDD),
    override val strokeColor: Color = Color(0xFF000000),
    override val disabledStrokeColor: Color = Color(0xFF000000),
    override val shadowStrokeColor: Color = Color(0xFF000000),
    override val shadowDisabledStrokeColor: Color = Color(0xFF000000),
) : ButtonColors

@Immutable
internal data class LightSecondaryButtonColors(
    override val containerColor: Color = Color(0xFFFFFFFF),
    override val disabledContainerColor: Color = Color(0xFFFFFFFF),
    override val shadowColor: Color = Color(0xFF000000),
    override val disabledShadowColor: Color = Color(0xFFFFFFFF),
    override val textColor: Color = Color(0xFF000000),
    override val disabledTextColor: Color = Color(0xFFDDDDDD),
    override val strokeColor: Color = Color(0xFF000000),
    override val disabledStrokeColor: Color = Color(0xFF000000),
    override val shadowStrokeColor: Color = Color(0xFFE6E7E8),
    override val shadowDisabledStrokeColor: Color = Color(0xFF000000),
) : ButtonColors

// DARK THEME BUTTONS:

@Immutable
internal data class DarkPrimaryButtonColors(
    override val containerColor: Color = Color(0xFF181716),
    override val disabledContainerColor: Color = Color(0xFF4D4D4D),
    override val shadowColor: Color = Color(0xFF181716),
    override val disabledShadowColor: Color = Color(0xFF181716),
    override val textColor: Color = Color(0xFFFFFFFF),
    override val disabledTextColor: Color = Color(0xFFFFFFFF),
    override val strokeColor: Color = Color(0xFFFFFFFF),
    override val disabledStrokeColor: Color = Color(0xFFFFFFFF),
    override val shadowStrokeColor: Color = Color(0xFFFFFFFF),
    override val shadowDisabledStrokeColor: Color = Color(0xFFFFFFFF),
) : ButtonColors

@Immutable
internal data class DarkSecondaryButtonColors(
    override val containerColor: Color = Color(0xFF181716),
    override val disabledContainerColor: Color = Color(0xFFFFFFFF),
    override val shadowColor: Color = Color(0xFF4D4D4D),
    override val disabledShadowColor: Color = Color(0xFFFFFFFF),
    override val textColor: Color = Color(0xFFFFFFFF),
    override val disabledTextColor: Color = Color(0xFFB7B7B7),
    override val strokeColor: Color = Color(0xFFFFFFFF),
    override val disabledStrokeColor: Color = Color(0xFF000000),
    override val shadowStrokeColor: Color = Color(0xFFFFFFFF),
    override val shadowDisabledStrokeColor: Color = Color(0xFF000000),
) : ButtonColors
