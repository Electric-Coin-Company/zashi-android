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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ZashiListItem(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ZashiListItemDefaults.contentPadding,
    titleIcons: ImmutableList<Int> = persistentListOf(),
    subtitle: String? = null,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    ZashiListItem(
        modifier = modifier,
        contentPadding = contentPadding,
        state =
            ZashiListItemState(
                text = stringRes(text),
                subtitle = subtitle?.let { stringRes(it) },
                isEnabled = isEnabled,
                onClick = onClick,
                icon = icon,
                titleIcons = titleIcons
            ),
    )
}

@Composable
fun ZashiListItem(
    state: ZashiListItemState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ZashiListItemDefaults.contentPadding,
) {
    ZashiListItem(
        modifier = modifier,
        contentPadding = contentPadding,
        leading = {
            ZashiListItemDefaults.LeadingItem(
                modifier = it,
                icon = state.icon,
                contentDescription = state.text.getValue()
            )
        },
        content = {
            ZashiListItemDefaults.ContentItem(
                modifier = it,
                text = state.text.getValue(),
                subtitle = state.subtitle?.getValue(),
                titleIcons = state.titleIcons
            )
        },
        trailing = {
            ZashiListItemDefaults.TrailingItem(
                modifier = it,
                isEnabled = state.isEnabled && state.onClick != null,
                contentDescription = state.text.getValue()
            )
        },
        onClick = state.onClick.takeIf { state.isEnabled }
    )
}

@Composable
private fun ZashiListLeadingItem(
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
private fun ZashiListTrailingItem(
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
private fun ZashiListContentItem(
    text: String,
    subtitle: String?,
    titleIcons: ImmutableList<Int>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row {
            Text(
                text = text,
                style = ZashiTypography.textMd,
                fontWeight = FontWeight.SemiBold,
                color = ZashiColors.Text.textPrimary
            )
            titleIcons.forEach {
                Spacer(Modifier.width(6.dp))
                Image(
                    modifier = Modifier.size(20.dp).clip(CircleShape),
                    painter = painterResource(it),
                    contentDescription = null,
                )
            }
        }
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
fun ZashiListItem(
    leading: @Composable (Modifier) -> Unit,
    content: @Composable (Modifier) -> Unit,
    trailing: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ZashiListItemDefaults.contentPadding,
    onClick: (() -> Unit)?
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp)) then
                if (onClick != null) {
                    Modifier.clickable(
                        indication = ripple(),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                        role = Role.Button,
                    )
                } else {
                    Modifier
                } then Modifier.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading(Modifier)
        Spacer(modifier = Modifier.width(16.dp))
        content(Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        trailing(Modifier)
    }
}

data class ZashiListItemState(
    val text: StringResource,
    @DrawableRes val icon: Int,
    val subtitle: StringResource? = null,
    val titleIcons: ImmutableList<Int> = persistentListOf(),
    val isEnabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
)

object ZashiListItemDefaults {
    val contentPadding: PaddingValues
        get() = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

    @Composable
    fun LeadingItem(
        icon: Int,
        contentDescription: String,
        modifier: Modifier = Modifier,
    ) = ZashiListLeadingItem(icon, contentDescription, modifier)

    @Composable
    fun TrailingItem(
        isEnabled: Boolean,
        contentDescription: String,
        modifier: Modifier = Modifier
    ) = ZashiListTrailingItem(isEnabled, contentDescription, modifier)

    @Composable
    fun ContentItem(
        text: String,
        subtitle: String?,
        titleIcons: ImmutableList<Int>,
        modifier: Modifier = Modifier,
    ) = ZashiListContentItem(text, subtitle, titleIcons, modifier)
}

@PreviewScreens
@Composable
private fun EnabledPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiListItem(
                text = "Test",
                subtitle = "Subtitle",
                icon = R.drawable.ic_radio_button_checked,
                onClick = {},
                titleIcons = persistentListOf(R.drawable.ic_radio_button_checked)
            )
        }
    }

@PreviewScreens
@Composable
private fun DisabledPreview() =
    ZcashTheme {
        BlankSurface {
            ZashiListItem(
                text = "Test",
                subtitle = "Subtitle",
                icon = R.drawable.ic_radio_button_checked,
                isEnabled = false,
                onClick = {},
            )
        }
    }
