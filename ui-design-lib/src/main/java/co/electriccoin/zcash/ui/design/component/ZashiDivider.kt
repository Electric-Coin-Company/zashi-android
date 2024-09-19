package co.electriccoin.zcash.ui.design.component

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors

@Composable
fun ZashiVerticallDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = ZashiColors.Surfaces.divider,
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
fun ZashiHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = ZashiColors.Surfaces.divider,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}
