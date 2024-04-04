package co.electriccoin.zcash.ui.screen.support.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview("Support")
@Composable
private fun PreviewSupport() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Support(
                isShowingDialog = false,
                setShowDialog = {},
                onBack = {},
                onSend = {},
                snackbarHostState = SnackbarHostState(),
                walletRestoringState = WalletRestoringState.NONE
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
@Suppress("LongParameterList")
fun Support(
    isShowingDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    walletRestoringState: WalletRestoringState,
) {
    val (message, setMessage) = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            SupportTopAppBar(
                onBack = onBack,
                showRestoring = walletRestoringState == WalletRestoringState.RESTORING,
            )
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
private fun SupportTopAppBar(
    onBack: () -> Unit,
    showRestoring: Boolean
) {
    SmallTopAppBar(
        restoringLabel =
            if (showRestoring) {
                stringResource(id = R.string.restoring_wallet_label)
            } else {
                null
            },
        titleText = stringResource(id = R.string.support_header),
        backText = stringResource(id = R.string.support_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.support_back_content_description),
        onBack = onBack,
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
    val focusRequester = remember { FocusRequester() }

    Column(
        Modifier
            .fillMaxHeight()
            .verticalScroll(
                rememberScrollState()
            )
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.zashi_logo_sign),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Body(
            text = stringResource(id = R.string.support_information),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        FormTextField(
            value = message,
            onValueChange = setMessage,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            placeholder = { Text(text = stringResource(id = R.string.support_hint)) },
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        PrimaryButton(
            onClick = { setShowDialog(true) },
            text = stringResource(id = R.string.support_send),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }

    LaunchedEffect(Unit) {
        // Causes the TextFiled to focus on the first screen visit
        focusRequester.requestFocus()
    }
}

@Composable
private fun SupportConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppAlertDialog(
        onConfirmButtonClick = onConfirm,
        confirmButtonText = stringResource(id = R.string.support_confirmation_dialog_ok),
        dismissButtonText = stringResource(id = R.string.support_confirmation_dialog_cancel),
        onDismissButtonClick = onDismiss,
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.support_confirmation_dialog_title),
        text =
            stringResource(
                id = R.string.support_confirmation_explanation,
                stringResource(id = R.string.app_name)
            )
    )
}
