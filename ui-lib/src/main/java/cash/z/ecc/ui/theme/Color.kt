@file:Suppress("MagicNumber")

package cash.z.ecc.ui.theme

import androidx.compose.ui.graphics.Color

object Dark {
    val backgroundStart = Color(0xff243155)
    val backgroundEnd = Color(0xff29365A)

    val textHeaderOnBackground = Color(0xffCBDCF2)
    val textBodyOnBackground = Color(0xFF93A4BE)
    val textPrimaryButton = Color(0xFF0F2341)
    val textSecondaryButton = Color(0xFF0F2341)
    val textTertiaryButton = Color.White
    val textNavigationButton = Color.Black
    val textCaption = Color(0xFF68728B)
    val textChipIndex = Color(0xFFFFB900)

    val primaryButton = Color(0xFFFFB900)
    val primaryButtonPressed = Color(0xFFFFD800)
    val primaryButtonDisabled = Color(0x33F4B728)

    val secondaryButton = Color(0xFFA7C0D9)
    val secondaryButtonPressed = Color(0xFFC8DCEF)
    val secondaryButtonDisabled = Color(0x33C8DCEF)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xB0C3D2BA)
    // TODO how does the invisible button show a disabled state?

    val navigationButton = Color(0xFFA7C0D9)
    val navigationButtonPressed = Color(0xFFC8DCEF)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFF929bb3)

    val callout = Color(0xFFa7bed8)
    val onCallout = Color(0xFF3d698f)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    val addressHighlightBorder = Color(0xFF525252)
    val addressHighlightUnified = Color(0xFFFFD800)
    val addressHighlightOrchard = Color(0xFFFFD800)
    val addressHighlightSapling = Color(0xFF1BBFF6)
    val addressHighlightTransparent = Color(0xFF97999A)
    val addressHighlightViewing = Color(0xFF504062)
}

object Light {
    val backgroundStart = Color(0xFFE3EFF9)
    val backgroundEnd = Color(0xFFD2E4F3)

    val textHeaderOnBackground = Color(0xff2D3747)
    val textBodyOnBackground = Color(0xFF7B8897)
    val textNavigationButton = Color(0xFF7B8897)
    val textPrimaryButton = Color(0xFFF2F7FC)
    val textSecondaryButton = Color(0xFF2E476E)
    val textTertiaryButton = Color(0xFF283559)
    val textCaption = Color(0xFF2D3747)
    val textChipIndex = Color(0xFFEE8592)

    // TODO The button colors are wrong for light
    val primaryButton = Color(0xFF263357)
    val primaryButtonPressed = Color(0xFFFFD800)
    val primaryButtonDisabled = Color(0x33F4B728)

    val secondaryButton = Color(0xFFE8F3FA)
    val secondaryButtonPressed = Color(0xFFFAFBFD)
    val secondaryButtonDisabled = Color(0xFFE6EFF8)

    val tertiaryButton = Color.Transparent
    val tertiaryButtonPressed = Color(0xFFFFFFFF)

    val navigationButton = Color(0xFFE3EDF7)
    val navigationButtonPressed = Color(0xFFE3EDF7)

    val progressStart = Color(0xFFF364CE)
    val progressEnd = Color(0xFFF8964F)
    val progressBackground = Color(0xFFbeccdf)

    val callout = Color(0xFFe6f0f9)
    val onCallout = Color(0xFFa1b8d0)

    val overlay = Color(0x22000000)
    val highlight = Color(0xFFFFD800)

    // [TODO #159]: The colors are wrong for light theme
    val addressHighlightBorder = Color(0xFF525252)
    val addressHighlightUnified = Color(0xFFFFD800)
    val addressHighlightOrchard = Color(0xFFFFD800)
    val addressHighlightSapling = Color(0xFF1BBFF6)
    val addressHighlightTransparent = Color(0xFF97999A)
    val addressHighlightViewing = Color(0xFF504062)
}
