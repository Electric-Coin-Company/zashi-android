package co.electriccoin.zcash.ui.design.theme.internal

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.R

@OptIn(ExperimentalTextApi::class)
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// We use bestEffort here to be able to get the closest font weight, if accidentally use
// an unspecified font weight and not the default one.
@OptIn(ExperimentalTextApi::class)
private val RubikFont = GoogleFont(name = "Rubik", bestEffort = true)

@OptIn(ExperimentalTextApi::class)
private val RubikFontFamily = FontFamily(
    Font(googleFont = RubikFont, fontProvider = provider, weight = FontWeight.Normal), // W400
    Font(googleFont = RubikFont, fontProvider = provider, weight = FontWeight.Medium), // W500
    Font(googleFont = RubikFont, fontProvider = provider, weight = FontWeight.SemiBold), // W600
    Font(googleFont = RubikFont, fontProvider = provider, weight = FontWeight.Bold) // W700
)

private val Zboto = FontFamily(
    Font(R.font.zboto, FontWeight.Normal)
)

// If you change this definition of our Typography, don't forget to check if you use only
// the defined font weights above, otherwise the closest one will be used.
internal val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

@Immutable
data class ExtendedTypography(
    val chipIndex: TextStyle,
    val listItem: TextStyle,
    val zecBalance: TextStyle
)

val LocalExtendedTypography = staticCompositionLocalOf {
    ExtendedTypography(
        chipIndex = Typography.bodyLarge.copy(
            fontSize = 10.sp,
            baselineShift = BaselineShift.Superscript,
            fontWeight = FontWeight.Bold
        ),
        listItem = Typography.bodyLarge.copy(
            fontSize = 24.sp
        ),
        zecBalance = TextStyle(
            fontFamily = Zboto,
            fontWeight = FontWeight.Normal,
            fontSize = 30.sp
        )
    )
}
