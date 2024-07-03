@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.chooseserver.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.type.ServerValidation
import cash.z.ecc.sdk.extension.isValid
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.RadioButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerTag
import co.electriccoin.zcash.ui.screen.chooseserver.validateCustomServerValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Preview("Choose Server")
@Composable
private fun PreviewChooseServer() {
    ZcashTheme(forceDarkMode = false) {
        ChooseServer(
            availableServers = emptyList<LightWalletEndpoint>().toImmutableList(),
            onBack = {},
            onServerChange = {},
            validationResult = ServerValidation.Valid,
            wallet = PersistableWalletFixture.new(),
            isShowingErrorDialog = false,
            setShowErrorDialog = {},
            isShowingSuccessDialog = false,
            setShowSuccessDialog = {},
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
        )
    }
}

@Composable
@Suppress("LongMethod", "LongParameterList")
fun ChooseServer(
    availableServers: ImmutableList<LightWalletEndpoint>,
    onBack: () -> Unit,
    onServerChange: (LightWalletEndpoint) -> Unit,
    validationResult: ServerValidation,
    wallet: PersistableWallet,
    isShowingErrorDialog: Boolean,
    setShowErrorDialog: (Boolean) -> Unit,
    isShowingSuccessDialog: Boolean,
    setShowSuccessDialog: (Boolean) -> Unit,
    topAppBarSubTitleState: TopAppBarSubTitleState,
) {
    val options =
        availableServers.toMutableList().apply {
            // Note that this comparison could lead to a match with any predefined server endpoint even though the user
            // previously pasted it as a custom one, which is fine for now and will be addressed when a dynamic
            //  server list obtaining is implemented.
            if (contains(wallet.endpoint)) {
                // We define the custom server as secured by default
                add(CUSTOM_SERVER_OPTION_INDEX, LightWalletEndpoint("", -1, true))
            } else {
                // Adding previously chosen custom endpoint
                add(CUSTOM_SERVER_OPTION_INDEX, wallet.endpoint)
            }
        }.toImmutableList()

    val (selectedOption, setSelectedOption) =
        rememberSaveable {
            mutableIntStateOf(options.indexOf(wallet.endpoint))
        }

    val initialCustomServerValue =
        options[CUSTOM_SERVER_OPTION_INDEX].run {
            if (options[CUSTOM_SERVER_OPTION_INDEX].isValid()) {
                stringResource(
                    R.string.choose_server_full_server_name,
                    options[CUSTOM_SERVER_OPTION_INDEX].host,
                    options[CUSTOM_SERVER_OPTION_INDEX].port
                )
            } else {
                ""
            }
        }
    val (customServerValue, setCustomServerValue) =
        rememberSaveable {
            mutableStateOf(initialCustomServerValue)
        }

    BlankBgScaffold(
        topBar = {
            ChooseServerTopAppBar(
                onBack = onBack,
                subTitleState = topAppBarSubTitleState,
            )
        },
        bottomBar = {
            ChooseServerBottomBar(
                customServerValue = customServerValue,
                onServerChange = onServerChange,
                options = options,
                selectedOption = selectedOption,
                setShowErrorDialog = setShowErrorDialog,
                validationResult = validationResult,
                wallet = wallet
            )
        }
    ) { paddingValues ->
        ChooseServerMainContent(
            customServerValue = customServerValue,
            modifier =
                Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
                    .fillMaxWidth(),
            options = options,
            selectedOption = selectedOption,
            setCustomServerValue = setCustomServerValue,
            setSelectedOption = setSelectedOption,
        )

        // Show validation popups
        if (isShowingErrorDialog && validationResult is ServerValidation.InValid) {
            ValidationErrorDialog(
                reason = validationResult.reason.message,
                onDone = { setShowErrorDialog(false) }
            )
        } else if (isShowingSuccessDialog) {
            SaveSuccessDialog(
                onDone = { setShowSuccessDialog(false) }
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun ChooseServerBottomBar(
    customServerValue: String,
    onServerChange: (LightWalletEndpoint) -> Unit,
    options: ImmutableList<LightWalletEndpoint>,
    selectedOption: Int,
    setShowErrorDialog: (Boolean) -> Unit,
    validationResult: ServerValidation,
    wallet: PersistableWallet,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.then(
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
            )
    ) {
        HorizontalDivider(
            thickness = DividerDefaults.Thickness,
            color = ZcashTheme.colors.primaryDividerColor
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        SaveButton(
            enabled = validationResult != ServerValidation.Running,
            options = options,
            customServerValue = customServerValue,
            onServerChange = {
                // Check if the selected is different from the current
                if (it != wallet.endpoint) {
                    onServerChange(it)
                }
            },
            setShowErrorDialog = setShowErrorDialog,
            selectedOption = selectedOption,
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingBig)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
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
        modifier = Modifier.testTag(ChooseServerTag.CHOOSE_SERVER_TOP_APP_BAR),
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

// When changing the following constants, be aware that they should not be the same
const val DEFAULT_SERVER_OPTION_INDEX = 0
const val CUSTOM_SERVER_OPTION_INDEX = 1

@Composable
@Suppress("LongParameterList")
private fun ChooseServerMainContent(
    customServerValue: String,
    options: ImmutableList<LightWalletEndpoint>,
    selectedOption: Int,
    setCustomServerValue: (String) -> Unit,
    setSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        ServerList(
            options = options,
            selectedOption = selectedOption,
            setSelectedOption = setSelectedOption,
            customServerValue = customServerValue,
            setCustomServerValue = setCustomServerValue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("LongParameterList", "LongMethod")
fun ServerList(
    options: ImmutableList<LightWalletEndpoint>,
    customServerValue: String,
    setCustomServerValue: (String) -> Unit,
    selectedOption: Int,
    setSelectedOption: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        options.forEachIndexed { index, endpoint ->
            val isSelected = index == selectedOption

            when (index) {
                DEFAULT_SERVER_OPTION_INDEX -> {
                    LabeledRadioButton(
                        endpoint = endpoint,
                        changeClick = { setSelectedOption(index) },
                        name =
                            stringResource(
                                id = R.string.choose_server_default_label,
                                stringResource(
                                    id = R.string.choose_server_full_server_name,
                                    endpoint.host,
                                    endpoint.port
                                )
                            ),
                        selected = isSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CUSTOM_SERVER_OPTION_INDEX -> {
                    Column(
                        modifier = Modifier.animateContentSize()
                    ) {
                        LabeledRadioButton(
                            endpoint = endpoint,
                            changeClick = { setSelectedOption(index) },
                            name = stringResource(id = R.string.choose_server_custom),
                            selected = isSelected,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isSelected) {
                            val focusManager = LocalFocusManager.current

                            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

                            FormTextField(
                                value = customServerValue,
                                onValueChange = {
                                    setCustomServerValue(it)
                                },
                                placeholder = {
                                    Text(text = stringResource(R.string.choose_server_textfield_hint))
                                },
                                keyboardActions =
                                    KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus(true)
                                        }
                                    ),
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Uri,
                                        imeAction = ImeAction.Done
                                    ),
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = ZcashTheme.dimens.spacingSmall)
                            )

                            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))
                        }
                    }
                }
                else -> {
                    LabeledRadioButton(
                        endpoint = endpoint,
                        changeClick = { setSelectedOption(index) },
                        name =
                            stringResource(
                                id = R.string.choose_server_full_server_name,
                                endpoint.host,
                                endpoint.port
                            ),
                        selected = isSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun String.toEndpoint(delimiter: String): LightWalletEndpoint {
    val parts = split(delimiter)
    return LightWalletEndpoint(parts[0], parts[1].toInt(), true)
}

@Composable
fun LabeledRadioButton(
    name: String,
    endpoint: LightWalletEndpoint,
    selected: Boolean,
    changeClick: (LightWalletEndpoint) -> Unit,
    modifier: Modifier = Modifier
) {
    RadioButton(
        text = name,
        selected = selected,
        onClick = { changeClick(endpoint) },
        modifier = modifier,
    )
}

@Composable
@Suppress("LongParameterList")
fun SaveButton(
    enabled: Boolean,
    customServerValue: String,
    onServerChange: (LightWalletEndpoint) -> Unit,
    options: ImmutableList<LightWalletEndpoint>,
    selectedOption: Int,
    modifier: Modifier = Modifier,
    setShowErrorDialog: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    PrimaryButton(
        enabled = enabled,
        showProgressBar = !enabled,
        text = stringResource(id = R.string.choose_server_save),
        onClick = {
            val selectedServer =
                if (selectedOption == CUSTOM_SERVER_OPTION_INDEX) {
                    if (!validateCustomServerValue(customServerValue)) {
                        setShowErrorDialog(true)
                        return@PrimaryButton
                    }

                    customServerValue.toEndpoint(context.getString(R.string.choose_server_custom_delimiter))
                } else {
                    options[selectedOption]
                }

            Twig.info { "Choose Server: Selected server: $selectedServer" }

            onServerChange(selectedServer)
        },
        modifier =
            modifier.then(
                Modifier.fillMaxWidth()
            )
    )
}

@Composable
fun ValidationErrorDialog(
    reason: String?,
    onDone: () -> Unit
) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.choose_server_validation_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = stringResource(id = R.string.choose_server_validation_dialog_error_text))

                if (!reason.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = reason,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.choose_server_validation_dialog_error_btn),
        onConfirmButtonClick = onDone
    )
}

@Composable
fun SaveSuccessDialog(onDone: () -> Unit) {
    Twig.info { "Succeed with saving the selected endpoint" }

    AppAlertDialog(
        title = stringResource(id = R.string.choose_server_save_success_dialog_title),
        text = stringResource(id = R.string.choose_server_save_success_dialog_text),
        confirmButtonText = stringResource(id = R.string.choose_server_save_success_dialog_btn),
        onConfirmButtonClick = onDone
    )
}
