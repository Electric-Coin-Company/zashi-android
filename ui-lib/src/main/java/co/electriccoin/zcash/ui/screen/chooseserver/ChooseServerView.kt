@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.RadioButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes

@Composable
fun ChooseServerView(
    state: ChooseServerState?,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    onBack: () -> Unit,
) {
    if (state == null) {
        CircularScreenProgressIndicator()
        return
    }

    BlankBgScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChooseServerTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
            )
        },
        bottomBar = {
            ChooseServerBottomBar(
                saveButtonState = state.saveButton
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingDefault,
                    start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                    end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                )
        ) {
            if (state.fastest.isLoading || state.all.servers.isNotEmpty()) {
                serverListItems(state.fastest)
            }
            serverListItems(state.all)
        }

        if (state.dialogState != null) {
            ErrorDialog(dialogState = state.dialogState)
        }
    }
}

@Composable
private fun ErrorDialog(dialogState: ServerDialogState) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    when (dialogState) {
        is ServerDialogState.SaveSuccess -> AppAlertDialog(state = dialogState.state)
        is ServerDialogState.Validation -> {
            AppAlertDialog(
                title = dialogState.state.title.getValue(),
                text = {
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(dialogState.state.text.getValue())

                        if (dialogState.reason != null) {
                            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                            Text(
                                text = dialogState.reason.getValue(),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                },
                confirmButtonText = dialogState.state.confirmButtonState?.text?.getValue(),
                onConfirmButtonClick = dialogState.state.confirmButtonState?.onClick
            )
        }
    }
}

@Composable
fun ChooseServerBottomBar(saveButtonState: ButtonState) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = ZcashTheme.colors.primaryDividerColor
        )
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
        PrimaryButton(
            state = saveButtonState,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig)
        )
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))
    }
}

@Composable
private fun ChooseServerTopAppBar(
    onBack: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.choose_server_title),
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = Modifier.testTag(CHOOSE_SERVER_TOP_APP_BAR),
        showTitleLogo = true,
        navigationAction = {
            TopAppBarBackNavigation(
                backText = stringResource(id = R.string.back_navigation).uppercase(),
                backContentDescriptionText = stringResource(R.string.back_navigation_content_description),
                onBack = onBack
            )
        }
    )
}

private fun LazyListScope.serverListItems(state: ServerListState) {
    item {
        Text(text = state.title.getValue())
    }

    items(
        items = state.servers,
        contentType = { item -> item.contentType },
    ) {
        when (it) {
            is ServerState.Custom ->
                CustomServerRadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = it
                )

            is ServerState.Default ->
                RadioButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.name.getValue(),
                    selected = it.isChecked,
                    onClick = it.onClick,
                )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomServerRadioButton(
    state: ServerState.Custom,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RadioButton(
            modifier = Modifier.fillMaxWidth(),
            text = state.name.getValue(),
            selected = state.isChecked,
            onClick = state.onClick,
        )

        AnimatedVisibility(visible = state.isChecked) {
            val focusManager = LocalFocusManager.current
            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
            FormTextField(
                state = state.newServerTextFieldState,
                placeholder = {
                    Text(text = stringResource(R.string.choose_server_textfield_hint))
                },
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ZcashTheme.dimens.spacingSmall)
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
        }
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun ChooseServerPreview(dialogState: ServerDialogState? = null) {
    var selectionIndex by remember { mutableIntStateOf(5) }
    ChooseServerView(
        state =
            ChooseServerState(
                fastest =
                    ServerListState.Fastest(
                        title = stringRes("Fastest Servers"),
                        servers =
                            (1..3).map {
                                ServerState.Default(
                                    name = stringRes("Some Server"),
                                    isChecked = selectionIndex == it,
                                    onClick = {
                                        selectionIndex = it
                                    },
                                )
                            },
                        isLoading = false
                    ),
                all =
                    ServerListState.All(
                        title = stringRes("All Servers"),
                        servers =
                            (4..<12).map {
                                if (it == 5) {
                                    ServerState.Custom(
                                        name = stringRes("Custom Server"),
                                        isChecked = selectionIndex == it,
                                        newServerTextFieldState =
                                            TextFieldState(
                                                value = stringRes(""),
                                                error = null,
                                                isEnabled = true,
                                                onValueChange = { },
                                            ),
                                        onClick = {
                                            selectionIndex = it
                                        }
                                    )
                                } else {
                                    ServerState.Default(
                                        name = stringRes("Some Server"),
                                        isChecked = selectionIndex == it,
                                        onClick = {
                                            selectionIndex = it
                                        },
                                    )
                                }
                            }
                    ),
                saveButton =
                    ButtonState(
                        text = stringRes("Save Button"),
                        isEnabled = true,
                        showProgressBar = false,
                        onClick = {},
                    ),
                dialogState = dialogState
            ),
        onBack = {},
        topAppBarSubTitleState = TopAppBarSubTitleState.None,
    )
}

@Composable
private fun ChooseServerPreviewValidationDialog() =
    ChooseServerPreview(
        dialogState =
            ServerDialogState.Validation(
                state =
                    AlertDialogState(
                        title = stringRes("title"),
                        text = stringRes("text"),
                    ),
                reason = stringRes("reason")
            )
    )

@Composable
private fun ChooseServerPreviewSaveSuccessDialog() =
    ChooseServerPreview(
        dialogState =
            ServerDialogState.SaveSuccess(
                state =
                    AlertDialogState(
                        title = stringRes("title"),
                        text = stringRes("text"),
                    ),
            )
    )

@Preview
@Composable
private fun ChooseServerPreviewLight() =
    ZcashTheme(forceDarkMode = false) {
        ChooseServerPreview()
    }

@Preview
@Composable
private fun ChooseServerPreviewValidationDialogLight() =
    ZcashTheme(forceDarkMode = false) {
        ChooseServerPreviewValidationDialog()
    }

@Preview
@Composable
private fun ChooseServerPreviewSaveSuccessDialogLight() =
    ZcashTheme(forceDarkMode = false) {
        ChooseServerPreviewSaveSuccessDialog()
    }

@Preview
@Composable
private fun ChooseServerPreviewDark() =
    ZcashTheme(forceDarkMode = true) {
        ChooseServerPreview()
    }

@Preview
@Composable
private fun ChooseServerPreviewValidationDialogDark() =
    ZcashTheme(forceDarkMode = true) {
        ChooseServerPreviewValidationDialog()
    }

@Preview
@Composable
private fun ChooseServerPreviewSaveSuccessDialogDark() =
    ZcashTheme(forceDarkMode = true) {
        ChooseServerPreviewSaveSuccessDialog()
    }

private const val CHOOSE_SERVER_TOP_APP_BAR = "choose_server_top_app_bar"
