package co.electriccoin.zcash.ui.screen.home.tor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.home.HomeMessageState
import co.electriccoin.zcash.ui.screen.home.HomeMessageWrapper

@Suppress("ModifierNaming")
@Composable
fun EnableTorMessage(
    contentPadding: PaddingValues,
    state: EnableTorMessageState,
    innerModifier: Modifier = Modifier,
) {
    HomeMessageWrapper(
        innerModifier = innerModifier,
        contentPadding = contentPadding,
        onClick = state.onClick,
        start = {
            Image(
                painter = painterResource(R.drawable.ic_message_tor),
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalContentColor.current)
            )
        },
        title = {
            Text(
                stringResource(R.string.home_message_tor_title)
            )
        },
        subtitle = {
            Text(
                text = stringResource(R.string.home_message_tor_subtitle),
            )
        },
        end = {
            ZashiButton(
                modifier = Modifier.height(36.dp),
                state =
                    ButtonState(
                        onClick = state.onButtonClick,
                        text = stringRes(stringResource(co.electriccoin.zcash.ui.design.R.string.general_review))
                    )
            )
        }
    )
}

class EnableTorMessageState(
    val onClick: () -> Unit,
    val onButtonClick: () -> Unit,
) : HomeMessageState

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        BlankSurface {
            EnableTorMessage(
                state =
                    EnableTorMessageState(
                        onClick = {},
                        onButtonClick = {}
                    ),
                contentPadding = PaddingValues()
            )
        }
    }
