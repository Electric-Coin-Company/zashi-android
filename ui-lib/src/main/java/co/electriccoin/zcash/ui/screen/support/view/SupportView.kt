package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Support")
@Composable
fun PreviewSupport() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Support(
                onBack = {},
                onSend = {},
                snackbarHostState = SnackbarHostState()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Support(
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    val (message, setMessage) = rememberSaveable { mutableStateOf("") }
    val (isShowingDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SupportTopAppBar(onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { setShowDialog(true) }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = stringResource(id = R.string.support_send)
                )
            }
        }
    ) { paddingValues ->
        SupportMainContent(
            paddingValues,
            message,
            setMessage
        )

        if (isShowingDialog) {
            SupportConfirmationDialog(
                onConfirm = { onSend(message) },
                onDismiss = { setShowDialog(false) }
            )
        }
    }
}

@Composable
private fun SupportTopAppBar(onBack: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text = stringResource(id = R.string.support_header)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.support_back_content_description)
                )
            }
        }
    )
}

@Composable
private fun SupportMainContent(
    paddingValues: PaddingValues,
    message: String,
    setMessage: (String) -> Unit
) {
    Column(
        Modifier
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        TextField(
            value = message,
            onValueChange = setMessage,
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.support_hint)) }
        )

        Text(stringResource(id = R.string.support_disclaimer))
    }
}

@Composable
private fun SupportConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(stringResource(id = R.string.support_confirmation_dialog_ok))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.support_confirmation_dialog_cancel))
            }
        },
        text = {
            Text(stringResource(id = R.string.support_confirmation_explanation, stringResource(id = R.string.app_name)))
        }
    )
}
