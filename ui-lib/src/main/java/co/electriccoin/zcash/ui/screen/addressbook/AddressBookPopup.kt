package co.electriccoin.zcash.ui.screen.addressbook

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiTextButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
internal fun AddressBookPopup(
    offset: IntOffset,
    transitionState: MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
    state: AddressBookState,
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismissRequest,
        offset = offset
    ) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter =
                fadeIn() +
                    slideInVertically(
                        spring(stiffness = Spring.StiffnessHigh),
                        initialOffsetY = { it }
                    ) +
                    scaleIn(
                        spring(
                            stiffness = Spring.StiffnessMedium,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    ),
            exit =
                fadeOut() +
                    scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                    slideOutVertically(targetOffsetY = { it / 2 }),
        ) {
            PopupContent(state = state, onDismissRequest = onDismissRequest)
        }
    }
}

@Composable
private fun PopupContent(
    state: AddressBookState,
    onDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(8.dp),
        color = ZashiColors.Surfaces.bgPrimary,
        border = BorderStroke(1.dp, ZashiColors.Surfaces.strokeSecondary)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.scanButton,
                res = R.drawable.ic_add_contact_qr,
                onDismissRequest = onDismissRequest
            )
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.manualButton,
                res = R.drawable.ic_add_contact_manual,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
private fun TextButton(
    state: ButtonState,
    @DrawableRes res: Int,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ZashiTextButton(
        modifier = modifier,
        onClick = {
            onDismissRequest()
            state.onClick()
        }
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(res),
            contentDescription = state.text.getValue(),
            colorFilter = ColorFilter.tint(ZashiColors.Text.textTertiary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = state.text.getValue(),
            style = ZashiTypography.textMd,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textPrimary
        )
    }
}

@PreviewScreens
@Composable
private fun PopupContentPreview() =
    ZcashTheme {
        PopupContent(
            state =
                AddressBookState(
                    onBack = {},
                    isLoading = false,
                    items = emptyList(),
                    scanButton =
                        ButtonState(
                            text = stringRes("Scan QR code"),
                        ),
                    manualButton =
                        ButtonState(
                            text = stringRes("Manual entry"),
                        ),
                    title = stringRes("Address book"),
                    info = null
                ),
            onDismissRequest = {}
        )
    }
