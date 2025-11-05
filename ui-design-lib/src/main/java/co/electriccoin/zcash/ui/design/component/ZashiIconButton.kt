package co.electriccoin.zcash.ui.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ZashiIconButton(
    state: IconButtonState,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick =
                if (state.hapticFeedbackType != null) {
                    {
                        haptic.performHapticFeedback(state.hapticFeedbackType)
                        state.onClick()
                    }
                } else {
                    state.onClick
                }
        ) {
            Icon(
                painter = painterResource(state.icon),
                contentDescription = state.contentDescription?.getValue(),
                tint = Color.Unspecified
            )
        }
        if (state.badge != null) {
            Badge(state.badge)
        }
    }
}

@Composable
private fun BoxScope.Badge(badge: StringResource) {
    Text(
        modifier =
            Modifier
                .size(20.dp)
                .background(ZashiColors.Utility.Gray.utilityGray900, CircleShape)
                .align(Alignment.TopEnd)
                .padding(top = 3.dp),
        text = badge.getValue(),
        textAlign = TextAlign.Center,
        color = ZashiColors.Surfaces.bgPrimary,
        style = ZashiTypography.textXs,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun ZashiImageButton(
    state: IconButtonState,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier =
            modifier.clickable(
                onClick =
                    if (state.hapticFeedbackType != null) {
                        {
                            haptic.performHapticFeedback(state.hapticFeedbackType)
                            state.onClick()
                        }
                    } else {
                        state.onClick
                    },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = state.isEnabled
            )
    ) {
        Image(
            modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
            painter = painterResource(state.icon),
            contentDescription = state.contentDescription?.getValue(),
            contentScale = ContentScale.Inside
        )

        if (state.badge != null) {
            Badge(state.badge)
        }
    }
}

@Immutable
data class IconButtonState(
    @DrawableRes val icon: Int,
    val contentDescription: StringResource? = null,
    val badge: StringResource? = null,
    val isEnabled: Boolean = true,
    val hapticFeedbackType: HapticFeedbackType? = null,
    val onClick: () -> Unit,
)

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        ZashiIconButton(
            state =
                IconButtonState(
                    icon = R.drawable.ic_item_keystone,
                    badge = stringRes("1"),
                    onClick = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun ImagePreview() =
    ZcashTheme {
        ZashiImageButton(
            state =
                IconButtonState(
                    icon = R.drawable.ic_item_keystone,
                    badge = stringRes("1"),
                    onClick = {}
                )
        )
    }
