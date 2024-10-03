package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiSettingsListItem(
    text: String,
    @DrawableRes icon: Int,
    subtitle: String? = null,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    ZashiSettingsListItem(
        state =
            ZashiSettingsListItemState(
                text = stringRes(text),
                subtitle = subtitle?.let { stringRes(it) },
                isEnabled = isEnabled,
                onClick = onClick
            ),
        icon = icon,
    )
}

@Composable
fun ZashiSettingsListItem(
    state: ZashiSettingsListItemState,
    @DrawableRes icon: Int
) {
    ZashiSettingsListItem(
        leading = { modifier ->
            ZashiSettingsListLeadingItem(
                modifier = modifier,
                icon = icon,
                contentDescription = state.text.getValue()
            )
        },
        content = { modifier ->
            ZashiSettingsListContentItem(
                modifier = modifier,
                text = state.text.getValue(),
                subtitle = state.subtitle?.getValue()
            )
        },
        trailing = { modifier ->
            ZashiSettingsListTrailingItem(
                modifier = modifier,
                isEnabled = state.isEnabled,
                contentDescription = state.text.getValue()
            )
        },
        onClick = state.onClick.takeIf { state.isEnabled }
    )
}

@Composable
fun ZashiSettingsListLeadingItem(
    icon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(icon),
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun ZashiSettingsListTrailingItem(
    isEnabled: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (isEnabled) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_right orDark R.drawable.ic_chevron_right_dark),
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun ZashiSettingsListContentItem(
    text: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = ZashiTypography.textMd,
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                style = ZashiTypography.textXs,
                color = ZashiColors.Text.textTertiary
            )
        }
    }
}

@Composable
fun ZashiSettingsListItem(
    leading: @Composable (Modifier) -> Unit,
    content: @Composable (Modifier) -> Unit,
    trailing: @Composable (Modifier) -> Unit,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    onClick: (() -> Unit)?
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp)) then
                if (onClick != null) {
                    Modifier.clickable(
                        indication = rememberRipple(),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                        role = Role.Button,
                    )
                } else {
                    Modifier
                } then Modifier.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        leading(Modifier)
        Spacer(modifier = Modifier.width(16.dp))
        content(Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        trailing(Modifier)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

data class ZashiSettingsListItemState(
    val text: StringResource,
    val subtitle: StringResource? = null,
    val isEnabled: Boolean = true,
    val onClick: () -> Unit = {},
)

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun EnabledPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiSettingsListItem(
                text = "Test",
                subtitle = "Subtitle",
                icon = R.drawable.ic_radio_button_checked,
                onClick = {}
            )
        }
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun DisabledPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiSettingsListItem(
                text = "Test",
                subtitle = "Subtitle",
                icon = R.drawable.ic_radio_button_checked,
                isEnabled = false,
                onClick = {}
            )
        }
    }
