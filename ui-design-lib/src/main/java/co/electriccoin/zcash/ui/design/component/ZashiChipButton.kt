package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiChipButton(
    state: ZashiChipButtonState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = ZashiColors.Btns.Tertiary.btnTertiaryBg,
    ) {
        Row(
            modifier =
                Modifier
                    .clickable(onClick = state.onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(state.icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ZashiColors.Btns.Tertiary.btnTertiaryFg)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = state.text.getValue(),
                color = ZashiColors.Btns.Tertiary.btnTertiaryFg,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

data class ZashiChipButtonState(
    @DrawableRes val icon: Int,
    val text: StringResource,
    val onClick: () -> Unit,
)

@PreviewScreens
@Composable
private fun ZashiChipButtonPreview() =
    ZcashTheme {
        ZashiChipButton(
            state =
                ZashiChipButtonState(
                    icon = R.drawable.ic_radio_button_checked,
                    text = stringRes("Test"),
                    onClick = {}
                )
        )
    }
