package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
fun ZashiBigIconButton(
    state: BigIconButtonState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .clickable(
                    indication = ripple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state.onClick,
                    role = Role.Button,
                ),
        shape = RoundedCornerShape(16.dp),
        color = ZashiColors.Surfaces.bgSecondary
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(state.icon),
                contentDescription = state.text.getValue(),
                tint = ZashiColors.Text.textPrimary
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
