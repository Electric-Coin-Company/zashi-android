package co.electriccoin.zcash.ui.screen.error

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.DialogState
import co.electriccoin.zcash.ui.design.component.ZashiScreenDialog
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun DialogView(state: ErrorState?) {
    ZashiScreenDialog(
        state =
            state?.let {
                DialogState(
                    title = it.title,
                    message = it.message,
                    positive = it.positive,
                    negative = it.negative,
                    onDismissRequest = it.onBack
                )
            },
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        DialogView(
            state =
                ErrorState(
                    title = stringRes("Error"),
                    message = stringRes("Something went wrong"),
                    positive =
                        ButtonState(
                            text = stringRes("Positive")
                        ),
                    negative =
                        ButtonState(
                            text = stringRes("Negative")
                        ),
                    onBack = {}
                )
        )
    }
