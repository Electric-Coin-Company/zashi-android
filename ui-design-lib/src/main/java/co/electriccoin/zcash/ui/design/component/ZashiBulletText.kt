package co.electriccoin.zcash.ui.design.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
fun ZashiBulletText(
    vararg bulletText: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ZashiTypography.textSm,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = ZashiColors.Text.textPrimary,
) {
    val normalizedStyle = style.copy(fontWeight = fontWeight)
    val bulletString = remember { "\u2022  " }
    val bulletTextMeasurer = rememberTextMeasurer()
    val bulletStringWidth =
        remember(normalizedStyle, bulletTextMeasurer) {
            bulletTextMeasurer.measure(text = bulletString, style = normalizedStyle).size.width
        }
    val bulletRestLine = with(LocalDensity.current) { bulletStringWidth.toSp() }
    val bulletParagraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = bulletRestLine))
    Text(
        modifier = modifier,
        text =
            buildAnnotatedString {
                withStyle(style = bulletParagraphStyle) {
                    bulletText.forEachIndexed { index, string ->
                        if (index != 0) {
                            appendLine()
                        }
                        append(bulletString)
                        append(string)
                    }
                }
            },
        style = style,
        fontWeight = fontWeight,
        color = color,
    )
}
