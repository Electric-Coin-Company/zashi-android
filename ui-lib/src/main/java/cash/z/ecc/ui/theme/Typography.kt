package cash.z.ecc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.sp
import cash.z.ecc.ui.R

private val Rubik = FontFamily(
    Font(R.font.rubik_regular, FontWeight.W400),
    Font(R.font.rubik_medium, FontWeight.W500)
)

@OptIn(ExperimentalUnitApi::class)
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
)

@Immutable
data class ExtendedTypography(
    val chipIndex: TextStyle,
    val listItem: TextStyle,
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
        )
    )
}
