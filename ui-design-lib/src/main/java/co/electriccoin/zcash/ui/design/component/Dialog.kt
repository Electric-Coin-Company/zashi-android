package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun LightAlertDialogComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        AppAlertDialog(
            title = "Light popup",
            text =
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Temporibus autem quibusdam et aut " +
                    "officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et " +
                    "molestiae non recusandae. Duis condimentum augue id magna semper rutrum.",
            confirmButtonText = "OK",
            dismissButtonText = "Cancel"
        )
    }
}

@Preview
@Composable
private fun NoButtonAlertDialogComposablePreview() {
    ZcashTheme(forceDarkMode = false) {
        AppAlertDialog(
            title = "Light popup",
            text =
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Temporibus autem quibusdam et aut " +
                    "officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et " +
                    "molestiae non recusandae. Duis condimentum augue id magna semper rutrum.",
        )
    }
}

@Preview
@Composable
private fun DarkAlertDialogComposablePreview() {
    ZcashTheme(forceDarkMode = true) {
        AppAlertDialog(
            title = "Dark no button popup",
            text =
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Temporibus autem quibusdam et aut " +
                    "officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et " +
                    "molestiae non recusandae. Duis condimentum augue id magna semper rutrum.",
            confirmButtonText = "OK",
            dismissButtonText = "Cancel"
        )
    }
}

// TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
// TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

@Composable
@Suppress("LongParameterList")
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    confirmButtonText: String? = null,
    onConfirmButtonClick: (() -> Unit)? = null,
    dismissButtonText: String? = null,
    onDismissButtonClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    title: String? = null,
    text: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        shape = RoundedCornerShape(corner = CornerSize(ZcashTheme.dimens.regularRippleEffectCorner)),
        onDismissRequest = onDismissRequest?.let { onDismissRequest } ?: {},
        confirmButton = {
            confirmButtonText?.let {
                NavigationButton(
                    text = confirmButtonText,
                    onClick = onConfirmButtonClick ?: {},
                    outerPaddingValues = PaddingValues(all = 0.dp)
                )
            }
        },
        dismissButton = {
            dismissButtonText?.let {
                NavigationButton(
                    text = dismissButtonText,
                    onClick = onDismissButtonClick ?: {},
                    outerPaddingValues = PaddingValues(all = 0.dp)
                )
            }
        },
        title = title?.let { { Text(text = title) } },
        text = text,
        icon = icon?.let { { Icon(imageVector = icon, null) } },
        properties = properties,
        modifier = modifier,
    )
}

@Composable
@Suppress("LongParameterList")
fun AppAlertDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    confirmButtonText: String? = null,
    onConfirmButtonClick: (() -> Unit)? = null,
    dismissButtonText: String? = null,
    onDismissButtonClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    title: String? = null,
    text: String? = null,
    properties: DialogProperties = DialogProperties()
) {
    AppAlertDialog(
        onDismissRequest = onDismissRequest?.let { onDismissRequest } ?: {},
        modifier = modifier,
        confirmButtonText = confirmButtonText,
        title = title,
        text = { text?.let { Text(text = text) } },
        icon = icon,
        properties = properties,
        onConfirmButtonClick = onConfirmButtonClick,
        dismissButtonText = dismissButtonText,
        onDismissButtonClick = onDismissButtonClick
    )
}

@Preview
@Composable
private fun NavigationButtonPreview() {
    ZcashTheme(forceDarkMode = false) {
        NavigationButton(
            onClick = {},
            text = "Test button",
        )
    }
}

@Preview
@Composable
private fun NavigationButtonDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        NavigationButton(
            onClick = {},
            text = "Dark button",
        )
    }
}

@Composable
private fun NavigationButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    outerPaddingValues: PaddingValues =
        PaddingValues(
            horizontal = ZcashTheme.dimens.spacingNone,
            vertical = ZcashTheme.dimens.spacingSmall
        ),
) {
    Button(
        onClick = onClick,
        modifier =
            modifier.then(
                Modifier
                    .padding(outerPaddingValues)
            ),
        colors = buttonColors(containerColor = ZcashTheme.colors.primaryColor)
    ) {
        Text(
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            text = text,
            color = ZcashTheme.colors.textPrimary
        )
    }
}
