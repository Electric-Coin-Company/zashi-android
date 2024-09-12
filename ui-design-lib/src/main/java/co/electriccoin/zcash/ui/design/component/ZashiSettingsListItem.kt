package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark

@Composable
fun ZashiSettingsListItem(
    state: ButtonState,
    @DrawableRes icon: Int,
    trailing: @Composable () -> Unit = {
        Image(
            painter = painterResource(R.drawable.ic_chevron_right orDark R.drawable.ic_chevron_right_dark),
            contentDescription = state.text.getValue(),
        )
    }
) {
    ZashiSettingsListItem(
        text = state.text.getValue(),
        icon = icon,
        trailing = trailing,
        onClick = state.onClick
    )
}

@Composable
fun ZashiSettingsListItem(
    text: String,
    @DrawableRes icon: Int,
    trailing: @Composable () -> Unit = {
        Image(
            painter = painterResource(R.drawable.ic_chevron_right orDark R.drawable.ic_chevron_right_dark),
            contentDescription = text,
        )
    },
    onClick: () -> Unit
) {
    ZashiSettingsListItem(
        leading = {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(icon),
                contentDescription = text
            )
        },
        content = {
            Text(
                text = text,
                style = ZcashTheme.typography.primary.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 16.sp
            )
        },
        trailing = trailing,
        onClick = onClick
    )
}

@Composable
fun ZashiSettingsListItem(
    leading: @Composable () -> Unit,
    content: @Composable () -> Unit,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                    role = Role.Button,
                )
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        leading()
        Spacer(modifier = Modifier.width(16.dp))
        content()
        Spacer(modifier = Modifier.weight(1f))
        trailing()
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ZashiSettingsListItemPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiSettingsListItem(
                text = "Test",
                icon = R.drawable.ic_radio_button_checked,
                onClick = {}
            )
        }
    }
