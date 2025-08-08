package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ZashiCheckboxCard(state: CheckboxState, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ZashiDimensions.Radius.radiusXl),
        border = BorderStroke(1.dp, ZashiColors.Surfaces.strokeSecondary),
    ) {
        ZashiCheckbox(
            state = state,
            spacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            textStyles =
                ZashiCheckboxDefaults.textStyles(
                    title =
                        ZashiTypography.textSm.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = ZashiColors.Text.textPrimary
                        ),
                    subtitle =
                        ZashiTypography.textSm.copy(
                            color = ZashiColors.Text.textTertiary
                        )
                )
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            ZashiCheckboxCard(
                state =
                    CheckboxState(
                        title = stringRes("title"),
                        subtitle = stringRes("subtitle"),
                        isChecked = false,
                        onClick = {}
                    )
            )
        }
    }
