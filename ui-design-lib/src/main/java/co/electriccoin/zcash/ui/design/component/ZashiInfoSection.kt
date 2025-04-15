package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Composable
fun ZashiInfoRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
) {
    Row {
        Image(
            painterResource(icon),
            contentDescription = null
        )
        Spacer(16.dp)
        Column {
            Spacer(2.dp)
            Text(
                text = title,
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium
            )
            Spacer(4.dp)
            Text(
                text = subtitle,
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textSm
            )
        }
    }
}