package co.electriccoin.zcash.ui

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.util.PreviewScreens

@Composable
internal fun UnavailableExchangeRatePopup(
    offset: IntOffset,
    transitionState: MutableTransitionState<Boolean>,
    onDismissRequest: () -> Unit,
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
                    slideInVertically(spring(stiffness = Spring.StiffnessHigh)) +
                    scaleIn(spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy)),
            exit =
                fadeOut() +
                    scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
                    slideOutVertically(),
        ) {
            PopupContent(onDismissRequest = onDismissRequest)
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun PopupContent(onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier =
                Modifier
                    .width(16.dp)
                    .height(8.dp)
                    .background(ZcashTheme.zashiColors.surfacePrimary, TriangleShape)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .background(ZcashTheme.zashiColors.surfacePrimary, RoundedCornerShape(8.dp))
                .padding(start = 12.dp, bottom = 12.dp),
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        color = ZcashTheme.zashiColors.textLight,
                        fontSize = 16.sp,
                        style = ZcashTheme.extendedTypography.restoringTopAppBarStyle,
                        text = stringResource(R.string.balances_exchange_rate_unavailable)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        color = ZcashTheme.zashiColors.textLightSupport,
                        style =
                            ZcashTheme.extendedTypography.restoringTopAppBarStyle.copy(
                                fontWeight = FontWeight.Medium
                            ),
                        fontSize = 14.sp,
                        text = stringResource(id = R.string.balances_exchange_rate_unavailable_subtitle)
                    )
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        painter = painterResource(R.drawable.ic_unavailable_exchange_rate_dialog_close),
                        contentDescription = "",
                        tint = ZcashTheme.zashiColors.textLightSupport
                    )
                }
            }
        }
    }
}

private val TriangleShape =
    GenericShape { size, _ ->

        // 1) Start at the top center
        moveTo(size.width / 2f, 0f)

        // 2) Draw a line to the bottom right corner
        lineTo(size.width, size.height)

        // 3) Draw a line to the bottom left corner and implicitly close the shape
        lineTo(0f, size.height)
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun PopupContentPreview() =
    ZcashTheme {
        PopupContent(onDismissRequest = {})
    }
