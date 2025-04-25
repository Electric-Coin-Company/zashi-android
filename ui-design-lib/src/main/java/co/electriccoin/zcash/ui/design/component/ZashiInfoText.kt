package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
fun ZashiInfoText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ZashiColors.Text.textTertiary,
    style: TextStyle = ZashiTypography.textXs,
    textAlign: TextAlign = TextAlign.Start,
) {
    Row(
        modifier = modifier,
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color)
        )
        Spacer(8.dp)
        Text(
            modifier =
                Modifier
                    .weight(1f),
            text = text,
            textAlign = textAlign,
            style = style,
            color = color
        )
    }
}
