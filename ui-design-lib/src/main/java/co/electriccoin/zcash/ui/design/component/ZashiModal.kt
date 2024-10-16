package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions

@Composable
fun ZashiModal(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ZashiDimensions.Radius.radius2xl),
        border = BorderStroke(1.dp, ZashiColors.Modals.surfaceStroke),
        color = ZashiColors.Modals.surfacePrimary
    ) {
        Box(
            modifier = Modifier
                .padding(ZashiDimensions.Spacing.spacing3xl)
                .wrapContentSize()
                .animateContentSize()
        ) {
            content()
        }
    }
}