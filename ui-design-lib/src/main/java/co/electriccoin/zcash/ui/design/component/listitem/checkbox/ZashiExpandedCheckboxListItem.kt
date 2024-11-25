package co.electriccoin.zcash.ui.design.component.listitem.checkbox

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiCheckboxIndicator
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDefaults
import co.electriccoin.zcash.ui.design.component.listitem.clickableModifier
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiExpandedCheckboxListItem(state: ZashiExpandedCheckboxListItemState, modifier: Modifier = Modifier) {
    ExpandedBaseListItem(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp),
        leading = {
            ZashiListItemDefaults.LeadingItem(
                modifier = it,
                icon = state.icon,
                contentDescription = state.title.getValue()
            )
        },
        content = {
            Column(
                modifier = it
            ) {
                Row {
                    Text(
                        text = state.title.getValue(),
                        style = ZashiTypography.textSm,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textPrimary
                    )
                }
                Text(
                    text = state.subtitle.getValue(),
                    style = ZashiTypography.textXs,
                    color = ZashiColors.Text.textTertiary
                )
            }
        },
        trailing = {
            ZashiCheckboxIndicator(state.isSelected)
        },
        below = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = state.info.title.getValue(),
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = state.info.subtitle.getValue(),
                    style = ZashiTypography.textXs,
                    color = ZashiColors.Text.textTertiary,
                )
            }
        },
        border = BorderStroke(
            1.dp,
            if (state.isSelected) {
                ZashiColors.Surfaces.bgAlt
            } else {
                ZashiColors.Surfaces.strokeSecondary
            }
        ),
        onClick = state.onClick,
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
private fun ExpandedBaseListItem(
    leading: @Composable (Modifier) -> Unit,
    content: @Composable (Modifier) -> Unit,
    trailing: @Composable (Modifier) -> Unit,
    below: @Composable ColumnScope.(Modifier) -> Unit,
    onClick: (() -> Unit)?,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    shape: Shape,
    border: BorderStroke? = null,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color.Transparent,
        border = border,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = clickableModifier(remember { MutableInteractionSource() }, onClick)
                .padding(contentPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                leading(Modifier)
                Spacer(modifier = Modifier.width(16.dp))
                content(Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                trailing(Modifier)
            }
            Spacer(Modifier.height(16.dp))
            below(Modifier)
        }
    }
}

data class ZashiExpandedCheckboxListItemState(
    val title: StringResource,
    val subtitle: StringResource,
    val icon: Int,
    val isSelected: Boolean,
    val info: ZashiExpandedCheckboxRowState,
    val onClick: () -> Unit
): CheckboxListItemState

data class ZashiExpandedCheckboxRowState(
    val title: StringResource,
    val subtitle: StringResource,
)

@Composable
@PreviewScreens
fun ExpandedPreviewChecked() = ZcashTheme {
    BlankSurface {
        ZashiExpandedCheckboxListItem(
            modifier = Modifier.fillMaxWidth(),
            state = ZashiExpandedCheckboxListItemState(
                title = stringRes("title"),
                subtitle = stringRes("subtitle"),
                icon = R.drawable.ic_radio_button_checked,
                isSelected = true,
                info = ZashiExpandedCheckboxRowState(
                    title = stringRes("title"),
                    subtitle = stringRes("subtitle")
                ),
                onClick = {}
            )
        )
    }
}

@Composable
@PreviewScreens
fun ExpandedPreviewUnchecked() = ZcashTheme {
    BlankSurface {
        ZashiExpandedCheckboxListItem(
            modifier = Modifier.fillMaxWidth(),
            state = ZashiExpandedCheckboxListItemState(
                title = stringRes("title"),
                subtitle = stringRes("subtitle"),
                icon = R.drawable.ic_radio_button_checked,
                isSelected = false,
                info = ZashiExpandedCheckboxRowState(
                    title = stringRes("title"),
                    subtitle = stringRes("subtitle")
                ),
                onClick = {}
            )
        )
    }
}
