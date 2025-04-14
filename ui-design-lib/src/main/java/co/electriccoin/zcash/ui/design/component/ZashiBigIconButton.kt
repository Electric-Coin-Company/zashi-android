package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.stringRes

@Suppress("MagicNumber")
@Composable
fun ZashiBigIconButton(
    state: BigIconButtonState,
    modifier: Modifier = Modifier,
) {
    val darkBgGradient =
        Brush.verticalGradient(
            0f to ZashiColors.Surfaces.strokeSecondary,
            .66f to ZashiColors.Surfaces.strokeSecondary.copy(alpha = 0.5f),
            1f to ZashiColors.Surfaces.strokeSecondary.copy(alpha = 0.25f),
        )

    val darkBorderGradient =
        Brush.verticalGradient(
            0f to ZashiColors.Surfaces.strokePrimary,
            1f to ZashiColors.Surfaces.strokePrimary.copy(alpha = 0f),
        )

    val backgroundModifier =
        Modifier.background(ZashiColors.Surfaces.bgPrimary) orDark
            Modifier.background(darkBgGradient)

    Surface(
        modifier = modifier,
        onClick = state.onClick,
        color = ZashiColors.Surfaces.bgPrimary,
        shape = RoundedCornerShape(22.dp),
        border =
            BorderStroke(.5.dp, ZashiColors.Utility.Gray.utilityGray100) orDark
                BorderStroke(.5.dp, darkBorderGradient),
        shadowElevation = 2.dp orDark 4.dp
    ) {
        Column(
            modifier = backgroundModifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(state.icon),
                contentDescription = state.text.getValue(),
                colorFilter = ColorFilter.tint(ZashiColors.Text.textPrimary)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = state.text.getValue(),
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textPrimary
            )
        }
    }
}

data class BigIconButtonState(
    val text: StringResource,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        ZashiBigIconButton(
            state =
                BigIconButtonState(
                    text = stringRes("Text"),
                    icon = R.drawable.ic_reveal,
                    onClick = {}
                )
        )
    }
