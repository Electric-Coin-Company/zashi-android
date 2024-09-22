package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiBadge(
    text: String,
    modifier: Modifier = Modifier,
    colors: ZashiBadgeColors = ZashiBadgeDefaults.successBadgeColors()
) {
    ZashiBadge(
        text = stringRes(text),
        modifier = modifier,
        colors = colors
    )
}

@Composable
fun ZashiBadge(
    text: StringResource,
    modifier: Modifier = Modifier,
    colors: ZashiBadgeColors = ZashiBadgeDefaults.successBadgeColors()
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = colors.container,
        border = BorderStroke(1.dp, colors.border),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = text.getValue(),
                style = ZcashTheme.extendedTypography.transactionItemStyles.contentMedium,
                fontSize = 14.sp,
                color = colors.text
            )
        }
    }
}

@Immutable
data class ZashiBadgeColors(
    val border: Color,
    val text: Color,
    val container: Color,
)

object ZashiBadgeDefaults {
    @Composable
    fun successBadgeColors(
        border: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess200,
        text: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess700,
        background: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess50,
    ) = ZashiBadgeColors(
        border = border,
        text = text,
        container = background,
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun BadgePreview() =
    ZcashTheme {
        ZashiBadge(text = stringRes("Badge"))
    }