@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.R.drawable
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.Badge
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.CircularScreenProgressIndicator
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.LottieProgress
import co.electriccoin.zcash.ui.design.component.RadioButton
import co.electriccoin.zcash.ui.design.component.RadioButtonCheckedContent
import co.electriccoin.zcash.ui.design.component.RadioButtonState
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.util.StringResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.exchangerate.BottomBar
import co.electriccoin.zcash.ui.screen.exchangerate.ZashiButton
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens

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
            )
        ) {
            if (state.fastest.servers.isEmpty() && state.fastest.isLoading) {
                item {
                    ServerLoading()
                }
            } else if (state.fastest.servers.isNotEmpty()) {
                serverListItems(state.fastest)
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            serverListItems(state.other)
        }

        if (state.dialogState != null) {
            ErrorDialog(dialogState = state.dialogState)
        }
    }
}

@Composable
private fun ServerLoading() {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieProgress(
            size = 32.dp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.choose_server_loading_title),
            style = ZcashTheme.typography.primary.headlineLarge,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.choose_server_loading_subtitle),
            fontSize = 14.sp,
            color = Color(0xFF646464)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ErrorDialog(dialogState: ServerDialogState) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    when (dialogState) {
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
    BottomBar {
        ZashiButton(
            state = saveButtonState,
            modifier =
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        )
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

@Suppress("LongMethod")
private fun LazyListScope.serverListItems(state: ServerListState) {
    item {
        when (state) {
            is ServerListState.Fastest -> FastestServersHeader(state = state)
            is ServerListState.Other -> OtherServersHeader(state = state)
        }
    }

    itemsIndexed(
        items = state.servers,
        contentType = { _, item -> item.contentType },
    ) { index, item ->
        when (item) {
            is ServerState.Custom ->
                CustomServerRadioButton(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp)
                        .then(
                            if (item.radioButtonState.isChecked) {
                                Modifier.background(Color(0xFFF4F4F6), RoundedCornerShape(12.dp))
                            } else {
                                Modifier
                            }
                        ),
                    state = item
                )

            is ServerState.Default ->
                RadioButton(
                    state = item.radioButtonState,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .then(
                            if (item.radioButtonState.isChecked && item.badge == null) {
                                Modifier.background(Color(0xFFF4F4F6), RoundedCornerShape(12.dp))
                            } else {
                                Modifier
                            }
                        ),
                    checkedContent = {
                        if (item.badge == null) {
                            RadioButtonCheckedContent(item.radioButtonState)
                        } else {
                            Image(
                                painter = painterResource(id = drawable.ic_radio_button_checked_variant),
                                contentDescription = item.radioButtonState.text.getValue(),
                            )
                        }
                    },
                    trailingContent = {
                        if (item.badge != null) {
                            Badge(text = item.badge)
                        }
                    }
                )
        }

        if (index != state.servers.lastIndex) {
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color(0xFFF7F7F5),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun FastestServersHeader(state: ServerListState.Fastest) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServerHeader(text = state.title)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = state.retryButton.onClick) {
                Text(
                    text = state.retryButton.text.getValue(),
                    style = ZcashTheme.typography.primary.headlineLarge,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))

                if (state.isLoading) {
                    LottieProgress()
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_retry),
                        contentDescription = state.retryButton.text.getValue()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun OtherServersHeader(state: ServerListState.Other) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        ServerHeader(text = state.title)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ServerHeader(text: StringResource) {
    Text(
        text = text.getValue(),
        style = ZcashTheme.typography.primary.headlineLarge,
        fontSize = 18.sp
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomServerRadioButton(
    state: ServerState.Custom,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RadioButton(
            state = state.radioButtonState,
            modifier = Modifier.fillMaxWidth(),
            trailingContent = {
                val iconAngle =
                    animateFloatAsState(
                        targetValue = if (state.radioButtonState.isChecked) 180f else 0f,
                        label = "iconAngle"
                    )
                Image(
                    modifier = Modifier.rotate(iconAngle.value),
                    painter = painterResource(id = R.drawable.ic_expand),
                    contentDescription = state.radioButtonState.text.getValue()
                )
            }
        )

        AnimatedVisibility(visible = state.radioButtonState.isChecked) {
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
                    .padding(
                        start = 52.dp,
                        end = 20.dp,
                        bottom = 16.dp
                    )
            )
        }
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun ChooseServerPreview(
    showFastestServerLoading: Boolean = true,
    dialogState: ServerDialogState? = null
) {
    var selectionIndex by remember { mutableIntStateOf(5) }
    val fastestServers =
        ServerListState.Fastest(
            title = stringRes("Fastest Servers"),
            servers =
            if (showFastestServerLoading) {
                (1..3).map {
                    ServerState.Default(
                        RadioButtonState(
                            text = stringRes("Some Server"),
                            isChecked = selectionIndex == it,
                            onClick = {
                                selectionIndex = it
                            },
                            subtitle = null,
                        ),
                        badge = null
                    )
                }
            } else {
                listOf()
            },
            retryButton =
            ButtonState(
                text = stringRes("Save Button"),
                onClick = {},
            ),
            isLoading = true,
        )
    ChooseServerView(
        state =
        ChooseServerState(
            fastest = fastestServers,
            other =
            ServerListState.Other(
                title = stringRes("Other Servers"),
                servers =
                (4..<12).map {
                    if (it == 5) {
                        ServerState.Custom(
                            RadioButtonState(
                                text = stringRes("Custom Server"),
                                isChecked = selectionIndex == it,
                                onClick = {
                                    selectionIndex = it
                                }
                            ),
                            newServerTextFieldState =
                            TextFieldState(
                                value = stringRes(""),
                                error = null,
                                isEnabled = true,
                                onValueChange = { },
                            ),
                        )
                    } else {
                        ServerState.Default(
                            RadioButtonState(
                                text = stringRes("Some Server"),
                                isChecked = selectionIndex == it,
                                onClick = {
                                    selectionIndex = it
                                },
                                subtitle = if (it == 6) stringRes("Default") else null,
                            ),
                            badge = if (it == 6) stringRes("Active") else null,
                        )
                    }
                }
            ),
            saveButton =
            ButtonState(
                text = stringRes("Save Button"),
                onClick = {},
            ),
            dialogState = dialogState
        ),
        onBack = {},
        topAppBarSubTitleState = TopAppBarSubTitleState.None,
    )
}

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ChooseServerPreviewValidationDialog() =
    ZcashTheme {
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
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ChooseServerPreviewLoading() =
    ZcashTheme {
        ChooseServerPreview(showFastestServerLoading = false)
    }

@Suppress("UnusedPrivateMember")
@PreviewScreens
@Composable
private fun ChooseServerPreviewData() =
    ZcashTheme {
        ChooseServerPreview()
    }

private const val CHOOSE_SERVER_TOP_APP_BAR = "choose_server_top_app_bar"