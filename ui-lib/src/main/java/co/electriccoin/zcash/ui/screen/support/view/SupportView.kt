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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme

@Preview
@Composable
private fun SupportPreview() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            Support(
                isShowingDialog = false,
                setShowDialog = {},
                onBack = {},
                onSend = {},
                snackbarHostState = SnackbarHostState(),
                topAppBarSubTitleState = TopAppBarSubTitleState.None,
            )
        }
    }
}

@Preview
@Composable
private fun SupportDarkPreview() {
    ZcashTheme(forceDarkMode = true) {
        BlankSurface {
            Support(
                isShowingDialog = false,
                setShowDialog = {},
                onBack = {},
                onSend = {},
                snackbarHostState = SnackbarHostState(),
                topAppBarSubTitleState = TopAppBarSubTitleState.None,
            )
        }
    }
}

@Preview("Support-Popup")
@Composable
private fun PreviewSupportPopup() {
    ZcashTheme(forceDarkMode = false) {
        SupportConfirmationDialog(
            onConfirm = {},
            onDismiss = {}
        )
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
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    val (message, setMessage) = rememberSaveable { mutableStateOf("") }

    BlankBgScaffold(
        topBar = {
            SupportTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
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
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        titleText = stringResource(id = R.string.support_header),
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        },
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
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
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

        // TODO [#1467]: Support screen - keep button above keyboard
        // TODO [#1467]: https://github.com/Electric-Coin-Company/zashi-android/issues/1467
        ZashiButton(
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
