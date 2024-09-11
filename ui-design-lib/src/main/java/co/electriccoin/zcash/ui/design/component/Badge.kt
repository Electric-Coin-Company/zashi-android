package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun Badge(
    text: StringResource,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    color: Color = ZcashTheme.zashiColors.utilitySuccess50,
    border: BorderStroke = BorderStroke(1.dp, ZcashTheme.zashiColors.utilitySuccess200),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        border = border
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = text.getValue(),
                style = ZcashTheme.extendedTypography.transactionItemStyles.contentMedium,
                fontSize = 14.sp,
                color = ZcashTheme.zashiColors.utilitySuccess700
            )
        }
    }
}

@Preview
@Composable
private fun BadgePreview() =
    ZcashTheme {
        Badge(text = stringRes("Badge"))
    }
