package co.electriccoin.zcash.ui.screen.home.messages

import androidx.annotation.IntRange
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.ZashiCircularProgressIndicator
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Suppress("ModifierNaming")
@Composable
fun WalletRestoringMessage(
    contentPadding: PaddingValues,
    state: WalletRestoringMessageState,
    innerModifier: Modifier = Modifier,
) {
    HomeMessageWrapper(
        innerModifier = innerModifier,
        contentPadding = contentPadding,
        onClick = state.onClick,
        start = {
            ZashiCircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                progressPercent = state.progress,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.home_message_restoring_title, state.progress),
            )
        },
        subtitle = {
            Text(
                text = stringResource(R.string.home_message_restoring_subtitle),
            )
        },
        end = null
    )
}

class WalletRestoringMessageState(
    @IntRange(from = 0, to = 100) val progress: Int,
    val onClick: () -> Unit
) : HomeMessageState

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        val progress by animateIntAsState(50, label = "progress", animationSpec = tween(10000))

        BlankSurface {
            WalletRestoringMessage(
                contentPadding = PaddingValues(16.dp),
                state =
                    WalletRestoringMessageState(
                        progress = progress,
                        onClick = {}
                    )
            )
        }
    }
