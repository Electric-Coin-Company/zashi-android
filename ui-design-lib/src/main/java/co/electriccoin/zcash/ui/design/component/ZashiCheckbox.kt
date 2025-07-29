package co.electriccoin.zcash.ui.design.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiCheckbox(
    text: StringResource,
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = ZashiDimensions.Spacing.spacingMd,
    textStyles: CheckboxTextStyles = ZashiCheckboxDefaults.textStyles()
) {
    ZashiCheckbox(
        state =
            CheckboxState(
                title = text,
                isChecked = isChecked,
                onClick = onClick,
            ),
        modifier = modifier,
        spacing = spacing,
        textStyles = textStyles
    )
}

@Composable
fun ZashiCheckbox(
    state: CheckboxState,
    modifier: Modifier = Modifier,
    spacing: Dp = ZashiDimensions.Spacing.spacingMd,
    contentPadding: PaddingValues = ZashiCheckboxDefaults.contentPadding,
    textStyles: CheckboxTextStyles = ZashiCheckboxDefaults.textStyles()
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = state.onClick)
                .padding(contentPadding)
    ) {
        ZashiCheckboxIndicator(state.isChecked)

        Spacer(spacing)

        Column {
            Text(
                text = state.title.getValue(),
                style = textStyles.title,
            )
            state.subtitle?.let {
                Spacer(2.dp)
                Text(
                    text = it.getValue(),
                    style = textStyles.subtitle
                )
            }
        }
    }
}

@Composable
fun ZashiCheckboxIndicator(isChecked: Boolean) {
    Box {
        Image(
            painter = painterResource(R.drawable.ic_zashi_checkbox),
            contentDescription = null
        )

        AnimatedVisibility(
            visible = isChecked,
            enter =
                scaleIn(
                    spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
            exit =
                scaleOut(
                    spring(
                        stiffness = Spring.StiffnessHigh,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_zashi_checkbox_checked),
                contentDescription = null
            )
        }
    }
}

@Immutable
data class CheckboxTextStyles(
    val title: TextStyle,
    val subtitle: TextStyle
)

object ZashiCheckboxDefaults {
    val spacing = ZashiDimensions.Spacing.spacingMd

    val contentPadding = PaddingValues(vertical = 12.dp)

    @Composable
    fun textStyles(
        title: TextStyle =
            ZashiTypography.textSm.copy(
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textPrimary
            ),
        subtitle: TextStyle =
            ZashiTypography.textSm.copy(
                color = ZashiColors.Text.textTertiary
            ),
    ) = CheckboxTextStyles(title = title, subtitle = subtitle)
}

@Immutable
data class CheckboxState(
    val title: StringResource,
    val isChecked: Boolean,
    val subtitle: StringResource? = null,
    val onClick: () -> Unit,
)

@PreviewScreens
@Composable
private fun ZashiCheckboxPreview() =
    ZcashTheme {
        var isChecked by remember { mutableStateOf(false) }
        BlankSurface {
            ZashiCheckbox(
                state =
                    CheckboxState(
                        title = stringRes("title"),
                        subtitle = stringRes("subtitle"),
                        isChecked = isChecked,
                        onClick = { isChecked = isChecked.not() }
                    )
            )
        }
    }
