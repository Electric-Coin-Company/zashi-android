package co.electriccoin.zcash.ui.screen.chooseserver.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.type.ServerValidation
import cash.z.ecc.sdk.extension.isValid
import cash.z.ecc.sdk.fixture.PersistableWalletFixture
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.RadioButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.SubHeader
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.chooseserver.ChooseServerTag
import co.electriccoin.zcash.ui.screen.chooseserver.validateCustomServerValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Preview("Choose Server")
@Composable
private fun PreviewChooseServer() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
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
                walletRestoringState = WalletRestoringState.NONE,
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
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
    walletRestoringState: WalletRestoringState,
) {
    Scaffold(
        topBar = {
            ChooseServerTopAppBar(
                onBack = onBack,
                showRestoring = walletRestoringState == WalletRestoringState.RESTORING,
            )
        }
    ) { paddingValues ->
        ChooseServerMainContent(
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
            availableServers = availableServers,
            onServerChange = onServerChange,
            setShowErrorDialog = setShowErrorDialog,
            validationResult = validationResult,
            wallet = wallet,
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
private fun ChooseServerTopAppBar(
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
        modifier = Modifier.testTag(ChooseServerTag.CHOOSE_SERVER_TOP_APP_BAR),
        showTitleLogo = true,
        backText = stringResource(id = R.string.choose_server_back).uppercase(),
        backContentDescriptionText = stringResource(R.string.choose_server_back_content_description),
        onBack = onBack,
    )
}

@Composable
@Suppress("LongMethod", "LongParameterList")
private fun ChooseServerMainContent(
    availableServers: ImmutableList<LightWalletEndpoint>,
    onServerChange: (LightWalletEndpoint) -> Unit,
    validationResult: ServerValidation,
    wallet: PersistableWallet,
    modifier: Modifier = Modifier,
    setShowErrorDialog: (Boolean) -> Unit,
) {
    val options =
        availableServers.toMutableList().apply {
            // Note that this comparison could lead to a match with any predefined server endpoint even though the user
            // previously pasted it as a custom one, which is fine for now and will be addressed when a dynamic
            //  server list obtaining is implemented.
            if (contains(wallet.endpoint)) {
                // We define the custom server as secured by default
                add(LightWalletEndpoint("", -1, true))
            } else {
                // Adding previously chosen custom endpoint
                add(wallet.endpoint)
            }
        }.toImmutableList()

    val (selectedOption, setSelectedOption) =
        rememberSaveable {
            mutableIntStateOf(options.indexOf(wallet.endpoint))
        }

    val initialCustomServerValue =
        options.last().run {
            if (options.last().isValid()) {
                stringResource(R.string.choose_server_textfield_value, options.last().host, options.last().port)
            } else {
                ""
            }
        }
    val (customServerValue, setCustomServerValue) =
        rememberSaveable {
            mutableStateOf(initialCustomServerValue)
        }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        SubHeader(
            text = stringResource(id = R.string.choose_server_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

        ServerList(
            options = options,
            selectedOption = selectedOption,
            setSelectedOption = setSelectedOption,
            customServerValue = customServerValue,
            setCustomServerValue = setCustomServerValue,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingLarge))

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
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.spacingUpLarge)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("LongParameterList")
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

            if (index == options.lastIndex) {
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
                    }
                }
            } else {
                LabeledRadioButton(
                    endpoint = endpoint,
                    changeClick = { setSelectedOption(index) },
                    name = stringResource(id = R.string.choose_server_textfield_value, endpoint.host, endpoint.port),
                    selected = isSelected,
                    modifier = Modifier.fillMaxWidth()
                )
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
        modifier = modifier
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
                if (selectedOption == options.lastIndex) {
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
