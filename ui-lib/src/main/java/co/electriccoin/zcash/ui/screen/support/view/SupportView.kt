package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.NavigationButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Support")
@Composable
private fun PreviewSupport() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Support(
                onBack = {},
                onSend = {},
                snackbarHostState = SnackbarHostState()
            )
        }
    }
}

@Preview("Support-Popup")
@Composable
private fun PreviewSupportPopup() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SupportConfirmationDialog(
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}

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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        SupportMainContent(
            message = message,
            setMessage = setMessage,
            setShowDialog = setShowDialog,
            modifier =
                Modifier.padding(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                    end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                )
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
@OptIn(ExperimentalMaterial3Api::class)
private fun SupportTopAppBar(onBack: () -> Unit) {
    TopAppBar(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SupportMainContent(
    message: String,
    setMessage: (String) -> Unit,
    setShowDialog: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(
                rememberScrollState()
            )
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Body(stringResource(id = R.string.support_information))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        FormTextField(
            value = message,
            onValueChange = setMessage,
            modifier =
                Modifier
                    .fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.support_hint)) }
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Body(stringResource(id = R.string.support_disclaimer))

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = { setShowDialog(true) },
            text = stringResource(id = R.string.support_send)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
private fun SupportConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        shape = RectangleShape,
        onDismissRequest = onDismiss,
        title = {
        },
        confirmButton = {
            NavigationButton(
                onClick = onConfirm,
                text = stringResource(id = R.string.support_confirmation_dialog_ok)
            )
        },
        dismissButton = {
            NavigationButton(
                onClick = onDismiss,
                text = stringResource(id = R.string.support_confirmation_dialog_cancel)
            )
        },
        text = {
            Text(stringResource(id = R.string.support_confirmation_explanation, stringResource(id = R.string.app_name)))
        }
    )
}
