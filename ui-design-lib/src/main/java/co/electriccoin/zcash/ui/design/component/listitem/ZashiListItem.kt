package co.electriccoin.zcash.ui.design.component.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.Itemizable
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ZashiListItem(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageResource? = null,
    badge: ImageResource? = null,
    type: ZashiListItemDesignType = ZashiListItemDesignType.PRIMARY,
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
            ListItemState(
                title = stringRes(title),
                subtitle = subtitle?.let { stringRes(it) },
                isEnabled = isEnabled,
                onClick = onClick,
                bigIcon = icon,
                smallIcon = badge,
                titleIcons = titleIcons,
                design = type
            ),
    )
}

@Composable
fun ZashiListItem(
    state: ListItemState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ZashiListItemDefaults.contentPadding,
    colors: ZashiListItemColors =
        when (state.design) {
            ZashiListItemDesignType.PRIMARY -> ZashiListItemDefaults.primaryColors()
            ZashiListItemDesignType.SECONDARY -> ZashiListItemDefaults.secondaryColors()
        },
    leading: (@Composable (Modifier) -> Unit)? =
        state.bigIcon?.let { icon ->
            {
                ZashiListItemDefaults.LeadingItem(
                    modifier = it,
                    icon = icon,
                    badge = state.smallIcon,
                    contentDescription = state.title.getValue()
                )
            }
        },
    trailing: (@Composable (Modifier) -> Unit)? =
        if (state.isEnabled && state.onClick != null) {
            {
                ZashiListItemDefaults.TrailingItem(
                    contentDescription = state.title.getValue(),
                    modifier = it
                )
            }
        } else {
            null
        },
    content: @Composable (Modifier) -> Unit = {
        ZashiListItemDefaults.ContentItem(
            modifier = it,
            text = state.title.getValue(),
            subtitle = state.subtitle?.getValue(),
            titleIcons = state.titleIcons,
            isEnabled = state.isEnabled
        )
    },
) {
    BaseListItem(
        modifier = modifier,
        contentPadding = contentPadding,
        leading = leading,
        content = content,
        trailing = trailing,
        onClick = state.onClick.takeIf { state.isEnabled },
        border = colors.borderColor.takeIf { !it.isUnspecified }?.let { BorderStroke(1.dp, it) },
        color = colors.backgroundColor
    )
}

@Composable
private fun ZashiListLeadingItem(
    bigIcon: ImageResource?,
    smallIcon: ImageResource?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if (bigIcon is ImageResource.ByDrawable) {
        Box(modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(bigIcon.resource),
                contentDescription = contentDescription,
                contentScale = ContentScale.FillHeight
            )
            if (smallIcon is ImageResource.ByDrawable) {
                Image(
                    modifier =
                        Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .offset(3.dp, 3.dp),
                    painter = painterResource(smallIcon.resource),
                    contentDescription = contentDescription,
                )
            }
        }
    }
}

@Composable
private fun ZashiListTrailingItem(
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = contentDescription,
        )
    }
}

@Suppress("MagicNumber")
@Composable
private fun ZashiListContentItem(
    text: String,
    subtitle: String?,
    titleIcons: ImmutableList<Int>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = ZashiTypography.textMd,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (isEnabled) {
                        ZashiColors.Text.textPrimary
                    } else {
                        ZashiColors.Text.textDisabled
                    }
            )
            titleIcons.forEachIndexed { index, icon ->
                if (index == 0) {
                    Spacer(Modifier.width(6.dp))
                    Image(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape),
                        painter = painterResource(icon),
                        contentDescription = null,
                    )
                } else {
                    val offset = (-index.toDouble().pow(2.0) - index).roundToInt().dp

                    Image(
                        modifier =
                            Modifier
                                .offset(x = offset)
                                .size(24.dp)
                                .border(2.dp, ZashiColors.Surfaces.bgPrimary, CircleShape)
                                .size(20.dp)
                                .padding(2.dp)
                                .clip(CircleShape),
                        painter = painterResource(icon),
                        contentDescription = null,
                    )
                }
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

@Immutable
data class ListItemState(
    val title: StringResource,
    val bigIcon: ImageResource? = null,
    val smallIcon: ImageResource? = null,
    val design: ZashiListItemDesignType = ZashiListItemDesignType.PRIMARY,
    val subtitle: StringResource? = null,
    val titleIcons: ImmutableList<Int> = persistentListOf(),
    val isEnabled: Boolean = true,
    val onClick: (() -> Unit)? = null,
    override val contentType: Any = "",
    override val key: Any = UUID.randomUUID().toString(),
) : Itemizable

data class ZashiListItemColors(
    val borderColor: Color,
    val backgroundColor: Color
)

object ZashiListItemDefaults {
    val contentPadding: PaddingValues
        get() = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

    @Composable
    fun LeadingItem(
        icon: ImageResource,
        badge: ImageResource?,
        contentDescription: String,
        modifier: Modifier = Modifier,
    ) = ZashiListLeadingItem(icon, badge, contentDescription, modifier)

    @Composable
    fun TrailingItem(
        contentDescription: String,
        modifier: Modifier = Modifier
    ) = ZashiListTrailingItem(contentDescription, modifier)

    @Composable
    fun ContentItem(
        text: String,
        subtitle: String?,
        titleIcons: ImmutableList<Int>,
        isEnabled: Boolean,
        modifier: Modifier = Modifier,
    ) = ZashiListContentItem(text, subtitle, titleIcons, isEnabled, modifier)

    @Composable
    fun primaryColors(
        borderColor: Color = Color.Unspecified,
        backgroundColor: Color = Color.Transparent
    ): ZashiListItemColors = ZashiListItemColors(borderColor = borderColor, backgroundColor = backgroundColor)

    @Composable
    fun secondaryColors(
        borderColor: Color = ZashiColors.Surfaces.strokeSecondary,
        backgroundColor: Color = Color.Transparent
    ): ZashiListItemColors = ZashiListItemColors(borderColor = borderColor, backgroundColor = backgroundColor)
}

@PreviewScreens
@Composable
private fun PrimaryPreview() =
    ZcashTheme {
        BlankSurface {
            Column(
                verticalArrangement = spacedBy(16.dp)
            ) {
                ZashiListItem(
                    title = "Test",
                    subtitle = "Subtitle",
                    icon = imageRes(R.drawable.ic_item_keystone),
                    badge = imageRes(R.drawable.ic_item_keystone),
                    onClick = {},
                    titleIcons =
                        persistentListOf(
                            R.drawable.ic_radio_button_checked,
                            R.drawable.ic_radio_button_checked,
                        )
                )
                ZashiListItem(
                    title = "Test",
                    subtitle = "Subtitle",
                    isEnabled = false,
                    onClick = {},
                )
            }
        }
    }

@PreviewScreens
@Composable
private fun SecondaryPreview() =
    ZcashTheme {
        BlankSurface {
            Column(
                verticalArrangement = spacedBy(16.dp)
            ) {
                ZashiListItem(
                    title = "Test",
                    subtitle = "Subtitle",
                    type = ZashiListItemDesignType.SECONDARY,
                    icon = imageRes(R.drawable.ic_radio_button_checked),
                    onClick = {},
                    titleIcons = persistentListOf(R.drawable.ic_radio_button_checked)
                )
                ZashiListItem(
                    title = "Test",
                    subtitle = "Subtitle",
                    type = ZashiListItemDesignType.SECONDARY,
                    isEnabled = false,
                    onClick = {},
                )
            }
        }
    }
