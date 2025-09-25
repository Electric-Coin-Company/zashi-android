package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
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
    contentPadding: PaddingValues = ZashiBadgeDefaults.contentPadding,
    leadingIconVector: Painter? = null,
    colors: ZashiBadgeColors = ZashiBadgeDefaults.successColors()
) {
    ZashiBadge(
        text = stringRes(text),
        leadingIconVector = leadingIconVector,
        modifier = modifier,
        colors = colors,
        contentPadding = contentPadding
    )
}

@Composable
fun ZashiBadge(
    text: StringResource,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ZashiBadgeDefaults.contentPadding,
    leadingIconVector: Painter? = null,
    colors: ZashiBadgeColors = ZashiBadgeDefaults.successColors()
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = colors.container,
        border = BorderStroke(1.dp, colors.border),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(contentPadding)
        ) {
            if (leadingIconVector != null) {
                Image(
                    painter = leadingIconVector,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
            }

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
    val contentPadding: PaddingValues
        get() = PaddingValues(horizontal = 10.dp, vertical = 4.dp)

    @Composable
    fun successColors(
        border: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess200,
        text: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess700,
        background: Color = ZashiColors.Utility.SuccessGreen.utilitySuccess50,
    ) = ZashiBadgeColors(
        border = border,
        text = text,
        container = background,
    )

    @Composable
    fun hyperBlueColors(
        border: Color = ZashiColors.Utility.HyperBlue.utilityBlueDark200,
        text: Color = ZashiColors.Utility.HyperBlue.utilityBlueDark700,
        background: Color = ZashiColors.Utility.HyperBlue.utilityBlueDark50,
    ) = ZashiBadgeColors(
        border = border,
        text = text,
        container = background,
    )

    @Composable
    fun errorColors(
        border: Color = ZashiColors.Utility.ErrorRed.utilityError200,
        text: Color = ZashiColors.Utility.ErrorRed.utilityError700,
        background: Color = ZashiColors.Utility.ErrorRed.utilityError50,
    ) = ZashiBadgeColors(
        border = border,
        text = text,
        container = background,
    )

    @Composable
    fun warningColors(
        border: Color = ZashiColors.Utility.WarningYellow.utilityOrange200,
        text: Color = ZashiColors.Utility.WarningYellow.utilityOrange700,
        background: Color = ZashiColors.Utility.WarningYellow.utilityOrange50,
    ) = ZashiBadgeColors(
        border = border,
        text = text,
        container = background,
    )
}

@PreviewScreens
@Composable
private fun BadgePreview() =
    ZcashTheme {
        ZashiBadge(
            text = stringRes("Badge"),
            leadingIconVector = painterResource(id = android.R.drawable.ic_input_add),
        )
    }
