package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowElevation by animateDpAsState(if (isPressed) 0.dp else (2.dp orDark 4.dp))
    val borderWidth by animateDpAsState(if (isPressed) 0.dp else 0.5.dp, animationSpec = spring())

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
            BorderStroke(borderWidth, ZashiColors.Utility.Gray.utilityGray100) orDark
                BorderStroke(borderWidth, darkBorderGradient),
        shadowElevation = shadowElevation,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = backgroundModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                painter = painterResource(state.icon),
                contentDescription = state.text.getValue()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = state.text.getValue(),
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Text.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Clip
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
