package co.electriccoin.zcash.ui.design.component

import android.view.WindowManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue

@Composable
fun ZashiScreenDialog(
    state: DialogState?,
    properties: DialogProperties = DialogProperties()
) {
    val parent = LocalView.current.parent
    SideEffect {
        (parent as? DialogWindowProvider)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
    }

    state?.let {
        Dialog(
            positive = state.positive,
            negative = state.negative,
            onDismissRequest = state.onDismissRequest,
            title = state.title,
            message = state.message,
            properties = properties,
        )
    }
}

@Composable
private fun Dialog(
    modifier: Modifier = Modifier,
    positive: ButtonState,
    negative: ButtonState,
    onDismissRequest: (() -> Unit),
    title: StringResource,
    message: StringResource,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ZashiButton(state = positive)
        },
        dismissButton = {
            ZashiButton(state = negative, colors = ZashiButtonDefaults.secondaryColors())
        },
        title = {
            Text(
                text = title.getValue(),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textXl,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = message.getValue(),
                color = ZashiColors.Text.textTertiary,
                style = ZashiTypography.textMd
            )
        },
        properties = properties,
        containerColor = ZashiColors.Surfaces.bgPrimary,
        titleContentColor = ZashiColors.Text.textPrimary,
        textContentColor = ZashiColors.Text.textPrimary,
        modifier = modifier,
    )
}

@Immutable
data class DialogState(
    val positive: ButtonState,
    val negative: ButtonState,
    val onDismissRequest: (() -> Unit),
    val title: StringResource,
    val message: StringResource,
)
